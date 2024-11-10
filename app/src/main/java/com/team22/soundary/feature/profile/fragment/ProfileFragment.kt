package com.team22.soundary.feature.profile.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.getCategoryMap
import com.team22.soundary.core.domain.model.stringListToEnumList
import com.team22.soundary.databinding.FragmentMypageBinding
import com.team22.soundary.feature.signup.presentation.ActivitySignIntro
import com.team22.soundary.feature.signup.presentation.SignupViewModel
import dagger.hilt.android.AndroidEntryPoint
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
        setEditButton()
        setDeleteAccountButton()
        setShareAlbumImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setProfileInfo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userInfo.collectLatest { user ->
                    setProfileImage(user.imageId)
                    binding.profileTextviewName.text = user.name
                    binding.profileTextviewDisplayid.text = getString(R.string.mypage_view_displayid , user.displayId)
                    binding.profileTextviewIntro.text = getString(R.string.mypage_view_statusmessage , user.statusMessage)

                    setCategory(user.label)
                }
            }
        }
    }

    private fun setProfileImage(image : String) {
        if(image != "") {
            Glide.with(requireContext())
                .load(image)
                .into(binding.profileImageview)
        }
    }

    private fun setCategory(label : List<String>) {
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

    private fun setShareAlbumImage() {
        lifecycleScope.launch {
            profileViewModel.sentShare.collectLatest {
                it.forEachIndexed { idx, share ->
                    if (idx < MAX_SENT_IMAGE) {
                        binding.albumGrid.getChildAt(idx).isVisible = true
                        Glide.with(requireContext())
                            .load(share.song.coverImage)
                            .into(binding.albumGrid.getChildAt(idx) as ImageView)
                    } else {
                        return@forEachIndexed
                    }
                }
            }
        }
    }

    private fun setEditButton() {
        binding.editButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame, ProfileEditedFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setDeleteAccountButton() {
        binding.logoutButton.setOnClickListener {
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
    }

    private fun deleteUserAccount() {
        lifecycleScope.launch {
            try {
                profileViewModel.deleteUserAccount()
                profileViewModel.clearToken()
                // 탈퇴 성공 시 메인 화면으로 이동하거나 로그아웃 처리
                navigateToLoginScreen()
            } catch (e: Exception) {
                Log.e("uin", "회원 탈퇴 실패: ${e.message}")
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(requireContext(), ActivitySignIntro::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            profileViewModel.getProfile()
            setCategory(profileViewModel.userInfo.value.label)
        }
    }

    companion object {
        private const val MAX_SENT_IMAGE = 3
    }
}