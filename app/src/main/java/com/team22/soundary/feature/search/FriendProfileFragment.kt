package com.team22.soundary.feature.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.team22.soundary.core.domain.model.Category
import com.team22.soundary.databinding.FragmentFriendProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FriendProfileFragment : Fragment() {
    private var _binding: FragmentFriendProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendProfileViewModel by viewModels()
    private lateinit var friendId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 친구 ID 가져오기
        friendId = arguments?.getString("FRIEND_ID") ?: return
        viewModel.loadFriendProfile(friendId)

        // 친구 프로필 정보 수신 및 UI 업데이트
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.friendProfile.collectLatest { profile ->
                profile?.let {
                    binding.userNameTextview.text = it.name
                    binding.userEmailTextview.text = "@" + it.displayId
                    // Glide를 사용하여 프로필 이미지 로드
                    Glide.with(this@FriendProfileFragment)
                        .load(it.image.toString())
                        .into(binding.profileImageView)
                }
            }
        }

        // 뒤로 가기 버튼 설정
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
