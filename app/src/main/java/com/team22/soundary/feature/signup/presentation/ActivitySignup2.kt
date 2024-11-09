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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
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

    private var selectedImageUri: Uri? = Uri.EMPTY

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignup2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val nickname = intent.extras?.getString(KEY_NICKNAME) ?: ""
        val displayId = intent.extras?.getString(KEY_DISPLAY_ID) ?: ""
        val label = intent.extras?.getStringArrayList(KEY_LABEL) ?: emptyList()

        setSignupButton(nickname, displayId, label)
        setGalleryLauncher()
        setPermissionLauncher()
        setupProfileImageClick()
    }

    private fun setGalleryLauncher() {
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                binding.signupImageviewProfileimage.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(this, "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPermissionLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryLauncher.launch(intent) // 권한이 허용된 경우 갤러리에 접근
                } else {
                    Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupProfileImageClick() {
        binding.signupImageviewProfileimage.setOnClickListener {
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
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 허용된 경우
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(intent)
            }
            else -> {
                // 권한 요청
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun setSignupButton(nickname: String, displayId: String, label: List<String>) {
         binding.signupButtonSubmit.setOnClickListener {
            var success = true
            lifecycleScope.launch{
                // 가입 완료 처리 후 MainActivity로 이동
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if(it.isSuccessful) Log.d("akuby21",it.result)

                    success = viewModel.updateUserInfo(
                        it.result,
                        User(
                            label = label,
                            displayId = displayId,
                            name = nickname,
                            statusMessage = binding.signupEdittextIntro.text.toString(),
                            image = selectedImageUri ?: Uri.EMPTY
                        )
                    )
                }
            }
            if (success) {
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
