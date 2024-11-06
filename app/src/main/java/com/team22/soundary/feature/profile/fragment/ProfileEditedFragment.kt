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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.team22.soundary.R
import com.team22.soundary.databinding.FragmentMypageEditBinding
import com.team22.soundary.feature.profile.domain.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.team22.soundary.feature.signup.presentation.ActivitySignup2
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileEditedFragment : Fragment() {
    private var _binding: FragmentMypageEditBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    private val selectedCategories = mutableSetOf<String>()
    private var selectedImageUri: Uri? = null

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
            binding.buttonDance
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
            profileViewModel.deleteLabel(category)
            button.setBackgroundColor(Color.WHITE)
            (button as Button).setTextColor(Color.BLACK)
        } else {
            selectedCategories.add(category)
            profileViewModel.addLabel(category)
            button.setBackgroundColor(Color.parseColor("#800080"))
            (button as Button).setTextColor(Color.WHITE)
        }
    }

    fun setupProfileImageClick() {
        binding.profileImageview.setOnClickListener( {
            if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
                )==PackageManager.PERMISSION_GRANTED
            ){
                accessGallery()
            } else{
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        })
    }

    // 권한이 부여된 후 갤러리에 접근하는 함수
    private fun accessGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, ProfileEditedFragment.GALLERY_REQUEST_CODE)
    }

    // 갤러리에서 이미지 선택 후 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                // 선택된 이미지를 ImageView에 표시
                binding.profileImageview.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ProfileEditedFragment.PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                // 권한이 모두 부여되었을 경우 갤러리 접근 가능
                accessGallery()
            } else {
                // 권한이 거부되었을 때 처리
                Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setProfileInfo (){
        lifecycleScope.launch {
            profileViewModel.userInfo.collect{
                binding.profileIntroEdit.setText(it.statusMessage)
                binding.profileNameEdit.setText(it.name)
                Glide.with(requireContext())
                    .load(it.image)
                    .into(binding.profileImageview)


            }
        }
    }

    fun setSaveButton(){
        binding.saveButton.setOnClickListener {
            //profileViewModel.updateProfile(selectedImageUri, selectedCategories.toList())
            profileViewModel.setProfile(
                name = binding.profileNameEdit.text.toString(),
                intro = binding.profileIntroEdit.text.toString(),
                profile= selectedImageUri ?: Uri.EMPTY
            )
            parentFragmentManager.popBackStack()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 100
        const val GALLERY_REQUEST_CODE = 101
    }
}
