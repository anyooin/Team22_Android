package com.team22.soundary.feature.main.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.team22.soundary.R
import com.team22.soundary.core.UiState
import com.team22.soundary.databinding.FragmentMainBinding
import com.team22.soundary.feature.share.presentation.share.ShareBottomSheet
import com.team22.soundary.extensions.getDiff
import com.team22.soundary.util.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var player: ExoPlayer
    private var shouldPreparePlayer: Boolean = true
    private var pausedPosition: Long = 0
    private lateinit var spinnerAdapter : ArrayAdapter<String>
    private lateinit var loadingDialog: LoadingDialog

    private var isInit = false

    private val ExoPlayer.isPaused: Boolean
        get() = !player.isPlaying && player.currentPosition != 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUiState()
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player.release()
        _binding = null
    }


    private fun setupUI() {
        setSongClickListener()
        setMediaPlayer()
        setShareButton()
        binding.likeButton.setOnClickListener {
            if(viewModel.isReceivedShare()){
                viewModel.likeMusic()
            } else{
                Snackbar.make(requireContext(), binding.main, "내가 공유한 노래는 좋아요를 누를 수 없습니다.", Snackbar.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun setShareButton(){
        binding.shareImageButton.setOnClickListener {
            val modal = ShareBottomSheet.newInstance(viewModel.getSongId())
            modal.show(parentFragmentManager, ShareBottomSheet.MAIN_BOTTOM_SHEET)
        }
    }

    private fun setSongClickListener() {
        binding.nextImageView.setOnClickListener {
            resetMediaPlayer()
            resetText()
            viewModel.onNextClicked()
        }

        binding.prevImageView.setOnClickListener {
            resetMediaPlayer()
            resetText()
            viewModel.onPrevClicked()
        }

    }

    private fun setSpinner(friendNameList : List<String>) {
        spinnerAdapter = ArrayAdapter(requireContext(), R.layout.main_spinner_item, friendNameList)
        binding.sortSpinner.adapter = spinnerAdapter
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                resetMediaPlayer()
                resetText()
                viewModel.onFriendChanged(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun resetText() {
        binding.messageTextView.visibility = View.INVISIBLE
        binding.messageTailImageView.visibility = View.INVISIBLE
        binding.instructionTextView.text = getString(
            R.string.main_not_enough_listen,
            MESSAGE_THRESHOLD
        )
    }

    private fun resetMediaPlayer() {
        player.removeMediaItem(0)
        pausedPosition = 0
        binding.mainProgressBar.progress = 0
        shouldPreparePlayer = true
    }

    private fun setMediaPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        setPlayerListener()

        binding.currentImageView.setOnClickListener {
            setPlayDrawable()
            if (player.isPlaying) {
                player.pause()
                pausedPosition = player.currentPosition
            } else {
                if (shouldPreparePlayer) preparePlayer()
                if (player.isPaused) player.seekTo(pausedPosition)
                player.play()
                updateMusicState()
            }
        }
    }

    private fun setPlayDrawable(){
        binding.playStateImageView.setImageResource(if(player.isPlaying) R.drawable.main_play_to_pause else R.drawable.main_pause_to_play)
        when(val drawable = binding.playStateImageView.drawable){
            is AnimatedVectorDrawable ->{
                drawable.start()
            }
            is AnimatedVectorDrawableCompat ->{
                drawable.start()
            }
        }
    }


    private fun setPlayerListener(){
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {
                    Player.STATE_READY -> {
                        binding.mainProgressBar.max =
                            player.duration.toInt() / PROGRESS_UPDATE_DELAY
                        shouldPreparePlayer = false
                    }

                    Player.STATE_ENDED -> {
                        binding.mainProgressBar.progress = 0
                        pausedPosition = 0L
                    }

                }
            }
        })
    }

    private fun updateMusicState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    if (player.isPlaying) {
                        updateProgressBar()
                        updateText()
                    }
                    delay(PROGRESS_UPDATE_DELAY.toLong())
                }
            }
        }
    }

    private fun updateProgressBar() {
        val progressValue = player.currentPosition / PROGRESS_UPDATE_DELAY
        binding.mainProgressBar.progress = progressValue.toInt()
    }

    private fun updateText() {
        val currentSecond = player.currentPosition / 1000
        updateMessageVisibility(currentSecond)
        updateInstructionText(currentSecond)
    }

    private fun updateMessageVisibility(currentSecond: Long) {
        if (currentSecond >= MESSAGE_THRESHOLD) {
            binding.messageTextView.visibility = View.VISIBLE
            binding.messageTailImageView.visibility = View.VISIBLE
        }
    }

    private fun updateInstructionText(currentSecond: Long) {
        val messageTimeLeft = MESSAGE_THRESHOLD - currentSecond

        binding.instructionTextView.text =
            if (messageTimeLeft > 0) getString(
                R.string.main_not_enough_listen,
                messageTimeLeft
            ) else getString(R.string.main_enough_listen)
    }

    private fun preparePlayer() {
        try {
            val uri = viewModel.getSongUri()
            if (uri != Uri.EMPTY && uri != null) {
                player.setMediaItem(
                    MediaItem.fromUri(uri)
                )
                player.prepare()
                shouldPreparePlayer = false
            }
        } catch (e: Exception) {
            when (e) {
                is IOException, is IllegalArgumentException -> {
                    Snackbar.make(requireContext(), binding.main, "에러 발생 : " + e.message, Snackbar.LENGTH_LONG)
                        .show()
                }

                else -> {}
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { uiState ->
                    when(uiState){
                        is UiState.Success -> {
                            Log.d("dataa",""+uiState.data)
                            if(!isInit){
                                if(uiState.data.friendNameList.isNotEmpty()){
                                    setSpinner(uiState.data.friendNameList)
                                    isInit = !isInit
                                }
                            }
                            binding.friendNameTextView.text = uiState.data.share.friend.name
                            binding.musicNameTextView.text = uiState.data.share.song.title
                            binding.singerTextView.text = uiState.data.share.song.artist.joinToString()
                            binding.messageTextView.text = uiState.data.share.message
                            binding.nextImageView.isGone = uiState.data.isLastSong
                            binding.prevImageView.isGone = uiState.data.isFirstSong
                            binding.likeButton.setImageResource(uiState.data.likeBackground)
                            binding.dayTextView.text =
                                uiState.data.share.sharedDate.getDiff()
                            uiState.data.share.friend.image?.let {
                                Glide.with(requireContext())
                                    .load(it)
                                    .circleCrop()
                                    .into(binding.friendPicImageView)
                            }
                            uiState.data.share.song.coverImage?.let {
                                Glide.with(requireContext())
                                    .load(it)
                                    .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                                    .into(binding.currentImageView)
                            }
                            spinnerAdapter = ArrayAdapter(requireContext(), R.layout.main_spinner_item, uiState.data.friendNameList)
                            loadingDialog.dismiss()
                        }
                        is UiState.Loading -> {
                            loadingDialog = LoadingDialog(requireContext())
                            loadingDialog.show()
                        }
                        is UiState.Error -> {
                            loadingDialog.dismiss()
                            Snackbar.make(requireContext(), binding.main, "에러 발생 : " + uiState.message, Snackbar.LENGTH_LONG)
                                .show()
                        }
                        is UiState.Empty -> {
                            loadingDialog.dismiss()
                            toggleUi()
                        }
                    }

                }
            }
        }
    }

    private fun toggleUi(){
        binding.emptyShareTextView.isGone = !binding.emptyShareTextView.isGone
        binding.controlImageView.isGone = !binding.controlImageView.isGone
        binding.currentImageView.isGone = !binding.currentImageView.isGone
        binding.prevImageView.isGone = !binding.prevImageView.isGone
        binding.nextImageView.isGone = !binding.nextImageView.isGone
        binding.likeButton.isGone = !binding.likeButton.isGone
        binding.shareImageButton.isGone = !binding.shareImageButton.isGone
        binding.friendNameTextView.isGone = !binding.friendNameTextView.isGone
        binding.friendPicImageView.isGone = !binding.friendPicImageView.isGone
        binding.dayTextView.isGone = !binding.dayTextView.isGone
        binding.instructionTextView.isGone = !binding.instructionTextView.isGone
        binding.mainProgressBar.isGone = !binding.mainProgressBar.isGone
    }

    fun checkAndRequestPermissions(activity: AppCompatActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우 요청
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    companion object {
        const val PROGRESS_UPDATE_DELAY = 50
        const val MESSAGE_THRESHOLD = 5
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    }
}