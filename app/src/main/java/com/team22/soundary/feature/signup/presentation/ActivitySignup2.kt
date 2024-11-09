package com.team22.soundary.feature.signup.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.team22.soundary.databinding.ActivitySignup2Binding
import com.team22.soundary.extensions.checkAndRequestPermissions
import com.team22.soundary.MainActivity  // MainActivity를 import
import com.team22.soundary.R
import com.team22.soundary.core.data.TokenRepositoryImpl
import com.team22.soundary.core.domain.model.Category
import com.team22.soundary.core.domain.model.User
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivitySignup2 : AppCompatActivity() {

    private lateinit var binding: ActivitySignup2Binding
    private val viewModel: SignupViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignup2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val nickname = intent.extras?.getString(KEY_NICKNAME) ?: ""
        val displayId = intent.extras?.getString(KEY_DISPLAY_ID) ?: ""
        val label = intent.extras?.getStringArrayList(KEY_LABEL) ?: emptyList()

        if (selectedImageUri == null) {
            selectedImageUri = Uri.parse("")
        }

        // 갤러리 접근 권한 요청
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // 권한이 부여되었을 경우 갤러리 열기
        binding.signupImageviewProfileimage.setOnClickListener {
            if (checkAndRequestPermissions(permissions, PERMISSION_REQUEST_CODE)) {
                accessGallery()
            }
        }

        setSignupButton(nickname, displayId, label)
    }

    // 권한이 부여된 후 갤러리에 접근하는 함수
    private fun accessGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    // 갤러리에서 이미지 선택 후 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.signupImageviewProfileimage.setImageURI(selectedImageUri)
        } else {
            Toast.makeText(this, "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                // 권한이 모두 부여되었을 경우 갤러리 접근 가능
                accessGallery()
            } else {
                // 권한이 거부되었을 때 처리
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSignupButton(nickname: String, displayId: String, label: List<String>) {
        var result = true
        binding.signupButtonSubmit.setOnClickListener {
            lifecycleScope.launch {
                // 가입 완료 처리 후 MainActivity로 이동
                result = viewModel.updateUserInfo(
                    User(
                        label = label,
                        displayId = displayId,
                        name = nickname!!,
                        statusMessage = binding.signupEdittextIntro.text.toString(),
                        image = selectedImageUri!!
                    )
                )
            }
            if (result) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "중복된 ID값", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ActivitySignup::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val GALLERY_REQUEST_CODE = 101

        const val KEY_NICKNAME = "nickname"
        const val KEY_DISPLAY_ID = "displayId"
        const val KEY_LABEL = "label"
    }

}
