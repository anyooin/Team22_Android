package com.team22.soundary.feature.share.presentation.music

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.Song
import com.team22.soundary.databinding.FragmentShareMusicBinding
import com.team22.soundary.feature.share.presentation.share.ShareFriendActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShareMusicFragment : Fragment() {
    private var _binding: FragmentShareMusicBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MusicListAdapter
    private val viewModel: MusicViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShareMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSpinner()
        setRecyclerView()
        setEditText()
    }

    private fun setSpinner() {
        binding.shareSortSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.share_spinner_item,
            resources.getStringArray(R.array.share_sort_array)
        )
        binding.shareSortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.changeSongListBySort(position)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
    }

    private fun setRecyclerView() {
        adapter = MusicListAdapter(requireContext(), object : MusicItemClickListener {
            override fun onClick(v: View, selectItem: Song) {
                val intent = Intent(requireContext(), ShareFriendActivity::class.java)
                intent.putExtra(ShareFriendActivity.KEY_PLATFORM_TRACK_ID, selectItem.id)
                intent.putExtra(ShareFriendActivity.KEY_TRACK_ID, selectItem.trackId)
                intent.putExtra(ShareFriendActivity.KEY_IMAGE, selectItem.coverImage)
                intent.putExtra(ShareFriendActivity.KEY_TITLE, selectItem.title)
                intent.putExtra(
                    ShareFriendActivity.KEY_SINGER,
                    selectItem.artist.joinToString(", ")
                )
                startActivity(intent)
                //requireActivity().finish()
            }
        })
        binding.shareMusicRecyclerview.adapter = adapter
        binding.shareMusicRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        lifecycleScope.launch {
            viewModel.songList.collectLatest {
                adapter.submitList(it)
            }
        }
    }

    private fun setEditText() {
        binding.shareSearchEdittext.addTextChangedListener {
            val text: String = binding.shareSearchEdittext.text.toString().trim()
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.changeSongListBySearch(text)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}