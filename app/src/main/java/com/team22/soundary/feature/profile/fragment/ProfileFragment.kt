package com.team22.soundary.feature.profile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.team22.soundary.R
import com.team22.soundary.databinding.FragmentMypageBinding
import com.team22.soundary.feature.profile.domain.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileInfo()
        // 연필 버튼 클릭 이벤트 설정
        binding.editButton.setOnClickListener {
            // ProfileEditedFragment로 이동
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.frame,
                    ProfileEditedFragment()
                ) // R.id.frame은 MainActivity의 frame 컨테이너
                .addToBackStack(null) // 백스택에 추가하여 뒤로 가기 버튼을 사용할 수 있게 함
                .commit()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.selectedCategories.collect { categories ->
                binding.categoryLabel.text = categories.joinToString(", ")
            }
        }
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setProfileInfo() {
        lifecycleScope.launch {
            profileViewModel.userInfo.collect {
                binding.profileTextviewName.text = it.name
                binding.profileTextviewIntro.text = it.statusMessage
                Glide.with(requireContext())
                    .load(it.image)
                    .into(binding.profileImageview)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            profileViewModel.getProfile()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            profileViewModel.userInfo.collectLatest {
                binding.profileTextviewName.setText(it.name)
                binding.profileTextviewIntro.setText(it.statusMessage)

                Glide.with(requireContext())
                    .load(it.image)
                    .into(binding.profileImageview)

            }
        }
    }
}