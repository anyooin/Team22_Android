package com.team22.soundary.feature.search.presentation.friend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.FragmentFriendSearchBinding
import com.team22.soundary.feature.search.FriendAdapter
import com.team22.soundary.feature.search.presentation.profile.FriendProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendSearchFragment : Fragment() {

    private lateinit var newFriendsAdapter: NewFriendAdapter
    private lateinit var myFriendsAdapter: FriendAdapter
    private lateinit var pendingFriendsAdapter: PendingFriendAdapter
    private var _binding: FragmentFriendSearchBinding? = null
    private val binding get() = _binding!!

    private val friendSearchViewModel: FriendSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newFriendsAdapter = createNewFriendAdapter()
        myFriendsAdapter = createFriendAdapter()
        pendingFriendsAdapter = createPendingFriendAdapter()

        setupRecyclerViews()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            friendSearchViewModel.loadFriends()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            friendSearchViewModel.newFriends.collectLatest { newFriends ->
                newFriendsAdapter.submitList(newFriends)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            friendSearchViewModel.myFriends.collectLatest { myFriends ->
                myFriendsAdapter.submitList(myFriends)
                updateFriendsCount(myFriends.size)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            friendSearchViewModel.pendingFriends.collectLatest { pendingFriends ->
                pendingFriendsAdapter.submitList(pendingFriends)
            }
        }
    }

    private fun updateFriendsCount(count: Int) {
        binding.friendsListTitle.text = "내 친구 ($count/20)"
    }

    private fun navigateToFriendProfile(friendId: String) {
        val fragment = FriendProfileFragment().apply {
            arguments = Bundle().apply {
                putString("FRIEND_ID", friendId)
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun createFriendAdapter() = FriendAdapter(
        context = requireContext(),
        onItemClick = { friend -> navigateToFriendProfile(friend.displayId) },
        onDeleteClick = { friend -> friendSearchViewModel.deleteFriend(friend) }
    )

    private fun createNewFriendAdapter() = NewFriendAdapter(
        context = requireContext(),
        onItemClick = { friend -> navigateToFriendProfile(friend.displayId) },
        onAcceptClick = { friend -> friendSearchViewModel.acceptFriend(friend) },
        onDeclineClick = { friend -> friendSearchViewModel.declineFriend(friend) }
    )

    private fun createPendingFriendAdapter() = PendingFriendAdapter(
        context = requireContext(),
        onItemClick = { friend -> navigateToFriendProfile(friend.displayId) }
    )

    private fun setupRecyclerViews() {
        binding.newFriendsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newFriendsAdapter
        }

        binding.myFriendsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myFriendsAdapter
        }

        binding.pendingFriendsRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = pendingFriendsAdapter
        }

        binding.pendingFriendsButton.setOnClickListener {
            if(binding.pendingFriendsRecyclerView.visibility == View.GONE) {
                binding.pendingFriendsRecyclerView.visibility = View.VISIBLE
                binding.pendingFriendsHeader.text = getString(R.string.pending_friend_close)
            } else {
                binding.pendingFriendsRecyclerView.visibility = View.GONE
                binding.pendingFriendsHeader.text =  getString(R.string.pending_friend_open)
            }
        }

        binding.searchEditText.setOnClickListener {
            val fragment = SearchResultFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame, fragment)
                .addToBackStack(null)
                .commit()
        }


    }
}
