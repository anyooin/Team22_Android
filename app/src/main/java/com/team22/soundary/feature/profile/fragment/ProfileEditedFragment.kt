package com.team22.soundary.feature.profile.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.team22.soundary.databinding.FragmentMypageEditBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.team22.soundary.core.domain.model.createImageMultipart
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEditedFragment : Fragment() {
    private var _binding: FragmentMypageEditBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    private val selectedCategoryList = mutableSetOf<String>()
    private var selectedImageUri: Uri? = Uri.EMPTY

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

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

        setProfileInfo()
        setupCategoryButtons(binding.profileCategoryGrid)
        setSaveButton()
        setGalleryLauncher()
        setPermissionLauncher()
        setupProfileImageClick()
        setBackButton()
        popFragment()
    }

    private fun setProfileInfo() {
        lifecycleScope.launch {
            profileViewModel.userInfo.collect { user ->
                setProfileImage(user.imageId)
                binding.profileNameEdit.setText(user.name)
                binding.profileIntroEdit.setText(user.statusMessage)
            }
        }
    }

    private fun setProfileImage(image: String) {
        if (image != "") {
            Glide.with(requireContext())
                .load(image)
                .into(binding.profileImageview)
        }
        profileViewModel.initImage(image)
    }

    private fun setupCategoryButtons(gridLayout: GridLayout) {
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)

            if (child is ToggleButton) {
                child.setOnClickListener {
                    if (child.isChecked) {
                        if (selectedCategoryList.size < 3) {
                            selectedCategoryList.add(child.text.toString())
                        } else {
                            child.isChecked = false
                            Toast.makeText(
                                requireContext(),
                                "최대 3개까지 선택 가능합니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        selectedCategoryList.remove(child.text.toString())
                    }
                }
            }
        }
    }

    private fun setSaveButton() {
        binding.saveButton.setOnClickListener {
            val nickname = binding.profileNameEdit.text.toString()
            if (nickname.isEmpty()) {
                Toast.makeText(requireContext(), "닉네임은 필수 입력칸입니다.", Toast.LENGTH_SHORT).show()
            } else if (selectedCategoryList.isEmpty()) {
                Toast.makeText(requireContext(), "카테고리를 하나 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                profileViewModel.editProfile(
                    nickname = binding.profileNameEdit.text.toString(),
                    intro = binding.profileIntroEdit.text.toString(),
                    imageId = profileViewModel.imageId.value
                )
                profileViewModel.setLabel(selectedCategoryList.toList())
            }
        }
    }

    private fun popFragment() {
        lifecycleScope.launch {
            profileViewModel.result.collectLatest { result ->
                if (result) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun setBackButton() {
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    selectedImageUri = result.data?.data
                    binding.profileImageview.setImageURI(selectedImageUri)
                    if (selectedImageUri != Uri.EMPTY) {
                        profileViewModel.uploadImage(
                            createImageMultipart(
                                requireContext(),
                                selectedImageUri
                            )
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun setPermissionLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryLauncher.launch(intent) // 권한이 허용된 경우 갤러리에 접근
                } else {
                    Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT)
                        .show()
                }
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
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 허용된 경우
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(intent)
            }

            else -> {
                // 권한 요청
                permissionLauncher.launch(permission)
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
