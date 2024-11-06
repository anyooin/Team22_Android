package com.team22.soundary.feature.profile.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.team22.soundary.databinding.FragmentMypageEditBinding
import com.team22.soundary.feature.profile.domain.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditedFragment : Fragment() {
    private var _binding: FragmentMypageEditBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    private val selectedCategories = mutableSetOf<String>()
    private var selectedImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                accessGallery() // 권한이 허용된 경우 갤러리에 접근
            } else {
                Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryButtons()
        setProfileInfo()
        setupProfileImageClick()
        setSaveButton()
    }

    private fun setupCategoryButtons() {
        val categoryButtons = listOf(
            binding.buttonHiphop,
            binding.buttonRock,
            binding.buttonPop,
            binding.buttonJpop,
            binding.buttonBallad,
            binding.buttonKpop
        )

        categoryButtons.forEach { button ->
            button.setOnClickListener {
                val category = button.text.toString()
                toggleCategorySelection(button, category)
            }
        }
    }

    private fun toggleCategorySelection(button: View, category: String) {
        if (selectedCategories.contains(category)) {
            selectedCategories.remove(category)
        } else {
            selectedCategories.add(category)
            (button as Button).setTextColor(Color.BLACK)
        }
    }

    private fun setupProfileImageClick() {
        binding.profileImageview.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 이상
                requestPermissionIfNeeded(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                // Android 12 이하
                requestPermissionIfNeeded(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun requestPermissionIfNeeded(permission: String) {
        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 허용된 경우
                accessGallery()
            }
            else -> {
                // 권한 요청
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    // 갤러리에 접근하는 함수
    private fun accessGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    // 갤러리에서 이미지 선택 후 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            if (selectedImageUri != null) {
                binding.profileImageview.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setProfileInfo() {
        lifecycleScope.launch {
            profileViewModel.userInfo.collect {
                binding.profileIntroEdit.setText(it.statusMessage)
                binding.profileNameEdit.setText(it.name)
                Glide.with(requireContext())
                    .load(it.image)
                    .into(binding.profileImageview)
            }
        }
    }

    private fun setSaveButton() {
        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                profileViewModel.setProfile(
                    name = binding.profileNameEdit.text.toString(),
                    intro = binding.profileIntroEdit.text.toString(),
                    profile = selectedImageUri ?: Uri.EMPTY
                )
                profileViewModel.addLabel(selectedCategories.toList())
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    companion object {
        const val GALLERY_REQUEST_CODE = 101
    }
}
