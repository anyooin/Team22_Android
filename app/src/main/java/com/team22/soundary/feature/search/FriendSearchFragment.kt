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
    private lateinit var searchResultAdapter: SearchResultAdapter  // 검색 결과 어댑터 추가
    private var _binding: FragmentFriendSearchBinding? = null
    private val binding get() = _binding!!
    private val user: String = "user" // TODO: 로그인 기능으로 수정 필요

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

        // 어댑터 초기화
        newFriendsAdapter = createFriendAdapter(onAcceptClick = { friend ->
            friendSearchViewModel.acceptFriend(friend) // `User` 객체만 전달
        }, onDeclineClick = { friend ->
            friendSearchViewModel.declineFriend(friend) // `User` 객체만 전달
        })

        myFriendsAdapter = createFriendAdapter(onDeleteClick = { friend ->
            friendSearchViewModel.deleteFriend(friend) // `User` 객체만 전달
        })

        pendingFriendsAdapter = PendingFriendAdapter { friend ->
            navigateToFriendProfile(friend.id)
        }

        // 검색 결과 어댑터 초기화
        searchResultAdapter = SearchResultAdapter { user ->
            friendSearchViewModel.requestFriend(user) // 검색 결과에서 추가된 사용자 처리
            Toast.makeText(requireContext(), "친구 신청을 보냈습니다.", Toast.LENGTH_SHORT).show()
        }

        // RecyclerView 설정
        setupRecyclerViews()

        // ViewModel의 StateFlow 관찰
        observeViewModel()

        // 검색 기능 추가
        setupSearchFunctionality()

        // 취소 버튼 클릭 시 검색창 초기화
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

        // 검색 결과 관찰 및 어댑터에 전달
        viewLifecycleOwner.lifecycleScope.launch {
            friendSearchViewModel.searchResultList.collectLatest { searchResults ->
                searchResultAdapter.submitList(searchResults)
            }
        }
    }

    private fun updateFriendsCount(count: Int) {
        binding.friendsListTitle.text = "내 친구 ($count/20)"
    }

    // 친구 프로필 화면으로 이동하는 함수로 중복 제거
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

    // 어댑터 생성 함수로 중복 제거
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

    // RecyclerView 설정을 함수로 분리
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

        // 대기 중인 친구 섹션 토글 기능
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

    // 검색 기능 설정 함수로 분리
    private fun setupSearchFunctionality() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isEmpty()) {
                    friendSearchViewModel.resetFilters() // 검색어가 없을 경우 필터 초기화
                } else {
                    friendSearchViewModel.filterFriends(query) // 검색어에 따라 친구 목록 필터링
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}