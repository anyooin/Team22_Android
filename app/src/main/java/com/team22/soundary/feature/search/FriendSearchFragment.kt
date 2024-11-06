package com.team22.soundary.feature.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendSearchFragment : Fragment() {

    private lateinit var newFriendsAdapter: FriendAdapter
    private lateinit var myFriendsAdapter: FriendAdapter
    private lateinit var pendingFriendsAdapter: PendingFriendAdapter
    private lateinit var searchResultAdapter: SearchResultAdapter
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

        // Initialize adapters
        newFriendsAdapter = createFriendAdapter(onAcceptClick = { friend ->
            friendSearchViewModel.acceptFriend(friend)
        }, onDeclineClick = { friend ->
            friendSearchViewModel.declineFriend(friend)
        })

        myFriendsAdapter = createFriendAdapter(onDeleteClick = { friend ->
            friendSearchViewModel.deleteFriend(friend)
        })

        pendingFriendsAdapter = PendingFriendAdapter { friend ->
            navigateToFriendProfile(friend.id)
        }

        searchResultAdapter = SearchResultAdapter(
            onRequestFriendClick = { user ->
                friendSearchViewModel.requestFriend(user)
                Toast.makeText(requireContext(), "친구 신청을 보냈습니다.", Toast.LENGTH_SHORT).show()
            },
            isFriendRequested = { user -> friendSearchViewModel.isFriend(user) } // 이미 친구 요청 상태인지 확인
        )

        setupRecyclerViews()

        observeViewModel()

        setupSearchFunctionality()

        binding.cancelButton.setOnClickListener {
            binding.searchEditText.text.clear()
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

        viewLifecycleOwner.lifecycleScope.launch {
            friendSearchViewModel.searchResultList.collectLatest { searchResults ->
                searchResultAdapter.submitList(searchResults)
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

    private fun createFriendAdapter(
        onAcceptClick: ((User) -> Unit)? = null,
        onDeclineClick: ((User) -> Unit)? = null,
        onDeleteClick: ((User) -> Unit)? = null
    ) = FriendAdapter(
        onItemClick = { friend -> navigateToFriendProfile(friend.id) },
        onAcceptClick = onAcceptClick,
        onDeclineClick = onDeclineClick,
        onDeleteClick = onDeleteClick
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
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = pendingFriendsAdapter
        }


        binding.pendingFriendsHeader.setOnClickListener {
            binding.pendingFriendsRecyclerView.visibility =
                if (binding.pendingFriendsRecyclerView.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        binding.searchEditText.setOnClickListener {
            val fragment = SearchResultFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frame, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isEmpty()) {
                    friendSearchViewModel.resetFilters()
                } else {
                    friendSearchViewModel.filterFriends(query)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
