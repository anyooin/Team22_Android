package com.team22.soundary.feature.profile.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.getCategoryMap
import com.team22.soundary.core.domain.model.stringListToEnumList
import com.team22.soundary.databinding.FragmentMypageBinding
import com.team22.soundary.feature.profile.domain.ProfileViewModel
import com.team22.soundary.feature.signup.presentation.ActivitySignIntro
import com.team22.soundary.feature.signup.presentation.SignupViewModel
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
    private val signupViewModel: SignupViewModel by viewModels()


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

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.selectedCategories.collect { categories ->
                binding.categoryLabel.text = categories.joinToString(", ") // 카테고리 텍스트 표시
            }
        }

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
        // "탈퇴하기" 버튼 클릭 이벤트 설정
        binding.logoutButton.setOnClickListener {
            showDeleteAccountDialog()
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

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("정말 탈퇴하시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                deleteUserAccount()
            }
            .setNegativeButton("아니요") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteUserAccount() {
        lifecycleScope.launch {
            try {
                profileViewModel.deleteUserAccount()
                profileViewModel.clearToken()
                // 탈퇴 성공 시 메인 화면으로 이동하거나 로그아웃 처리
                navigateToLoginScreen()
            } catch (e: Exception) {
                Log.e("ProfileFragment", "회원 탈퇴 실패: ${e.message}")
            }
        }
    }
    fun setProfileInfo() {
        val categoryButtons = listOf(
            binding.hiphop,
            binding.rock,
            binding.pop,
            binding.jpop,
            binding.rnb,
            binding.kpop
        )
        lifecycleScope.launch {
            profileViewModel.userInfo.collect {
                binding.profileTextviewName.text = it.name
                binding.profileTextviewIntro.text = it.statusMessage
                Glide.with(requireContext())
                    .load(it.image)
                    .into(binding.profileImageview)

                val categoryList = stringListToEnumList(it.label)
                val categoryMap = getCategoryMap()

                categoryList.forEach {
                    categoryMap[it]?.let{
                        categoryButtons[it].isVisible = true
                    }
                }

                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("akuby21", "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        user.kakaoAccount?.let {account ->
                            account.email?.let{
                                binding.profileTextviewEmail.text = it
                            }
                        }
                    }
                }
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

            profileViewModel.sentShare.collectLatest {
                it.forEachIndexed{ idx,share ->
                    if(idx < MAX_SENT_IMAGE){
                        val targetUri = share.song.coverImage
                        when(idx) {
                            0 -> {
                                binding.imageOne.isVisible = true
                                Glide.with(requireContext())
                                    .load(targetUri)
                                    .into(binding.imageOne)
                            }
                            1 -> {
                                binding.imageTwo.isVisible = true
                                Glide.with(requireContext())
                                    .load(targetUri)
                                    .into(binding.imageTwo)
                            }
                            2 -> {
                                binding.imageThree.isVisible = true
                                Glide.with(requireContext())
                                    .load(targetUri)
                                    .into(binding.imageThree)
                            }
                        }
                    } else{
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(requireContext(), ActivitySignIntro::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    companion object{
        private const val MAX_SENT_IMAGE = 3

        const val CATEGORY_HIPHOP = 0
        const val CATEGORY_ROCK = 0
        const val CATEGORY_POP = 0
        const val CATEGORY_JPOP = 0
        const val CATEGORY_RNB = 0
        const val CATEGORY_KPOP = 0


    }
}