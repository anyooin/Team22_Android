package com.team22.soundary.feature.share.presentation.share

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.team22.soundary.MainActivity
import com.team22.soundary.databinding.ActivityShareFriendBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShareFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShareFriendBinding

    private lateinit var adapter: FriendListAdapter
    private val viewModel: ShareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBackButton()
        setMusicInfoText()
        setRecyclerView()
        setComment()
        setAddFriendButton()
        setSendButton()
    }

    private fun setBackButton() {
        binding.shareFriendBackButton.setOnClickListener {
            finish()
        }
    }

    private fun setMusicInfoText() {
        val image: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_IMAGE, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(KEY_IMAGE)
        }
        val music: String? = intent.extras?.getString(KEY_TITLE)
        val singer: String? = intent.extras?.getString(KEY_SINGER)

        Glide.with(applicationContext)
            .load(image)
            .into(binding.shareMusicImageview)
        binding.shareMusicTextview.text = music
        binding.shareSingerTextview.text = singer
    }

    private fun setRecyclerView() {
        adapter = FriendListAdapter()
        binding.shareFriendRecyclerview.adapter = adapter
        binding.shareFriendRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            viewModel.selectedFriendIds.collect {
                adapter.submitList(viewModel.getSelectedFriends())
                binding.shareSendButton.text = viewModel.getButtonText()
            }
        }
    }

    private fun setComment() {
        lifecycleScope.launch {
            viewModel.comment.collect {
                binding.shareCommentEdittext.setText(it)
            }
        }
    }

    private fun setAddFriendButton() {
        binding.shareAddFriend.setOnClickListener {
            viewModel.setComment(binding.shareCommentEdittext.text.toString())
            val modal = ShareBottomSheet()
            modal.show(supportFragmentManager, ShareBottomSheet.SHARE_BOTTOM_SHEET)
        }
    }

    private fun setSendButton() {
        val songId: String = intent.extras?.getString(KEY_ID) ?: ""
        binding.shareSendButton.text = viewModel.getButtonText()
        binding.shareSendButton.setOnClickListener {
            if(viewModel.isAnyFriendSelected()) {
                viewModel.setComment(binding.shareCommentEdittext.text.toString())
                viewModel.shareSongToFriends(songId)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // FLAG_ACTIVITY_CLEAR_TOP 사용?
            } else {
                Toast.makeText(this, "친구를 1명 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val KEY_ID = "id"
        const val KEY_IMAGE = "image"
        const val KEY_TITLE = "title"
        const val KEY_SINGER = "singer"
    }
}