package com.team22.soundary.feature.search.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.getCategoryMap
import com.team22.soundary.core.domain.model.stringListToEnumList
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
                    binding.userEmailTextview.text =
                        getString(R.string.mypage_view_displayid, it.displayId)
                    binding.statusMessageTextview.text =
                        getString(R.string.mypage_view_statusmessage, it.statusMessage)
                    // Glide를 사용하여 프로필 이미지 로드
                    if (it.imageId != "") {
                        Glide.with(this@FriendProfileFragment)
                            .load(it.imageId)
                            .into(binding.profileImageview)
                    }
                    setCategory(it.label)
                }
            }
        }

        // 뒤로 가기 버튼 설정
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setCategory(label: List<String>) {
        val categoryList = stringListToEnumList(label)
        val categoryMap = getCategoryMap()

        for (i in 0 until binding.categoryGrid.childCount) {
            binding.categoryGrid.getChildAt(i).visibility = View.GONE
        }

        categoryList.forEach { category ->
            categoryMap[category]?.let { index ->
                binding.categoryGrid.getChildAt(index).visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
