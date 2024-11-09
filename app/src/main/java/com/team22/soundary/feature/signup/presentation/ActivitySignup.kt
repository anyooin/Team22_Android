package com.team22.soundary.feature.signup.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.user.UserApiClient
import com.team22.soundary.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitySignup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val selectedCategoryList = mutableSetOf<String>()
    //private val viewModel: SignupViewModel by viewModels() // ViewModel 초기화

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEmailId("default@example.com")
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("akuby21", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                user.kakaoAccount?.let { account ->
                    account.email?.let {
                        setEmailId(it)
                    }
                }
            }
        }

        setupCategoryButtons(binding.signupCategoryGrid)
        setNextSignupButton()
    }

    private fun setEmailId(email: String) {
        val emailId = email.substringBefore("@")
        binding.signupEdittextEmail.setText(emailId)
        binding.signupEdittextEmail.isEnabled = false
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
                            Toast.makeText(this, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        selectedCategoryList.remove(child.text.toString())
                    }
                }
            }
        }
    }

    private fun setNextSignupButton() {
        binding.signupButtonContinue.setOnClickListener {
            val nickname = binding.signupEdittextNickname.text.toString()
            val displayId = binding.signupEdittextEmail.text.toString()

            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임은 필수 입력칸입니다.", Toast.LENGTH_SHORT).show()
            } else if (selectedCategoryList.isEmpty()) {
                Toast.makeText(this, "카테고리를 하나 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ActivitySignup2::class.java).apply {
                    this.putExtra(ActivitySignup2.KEY_NICKNAME, nickname)
                    this.putExtra(ActivitySignup2.KEY_DISPLAY_ID, displayId)
                    this.putStringArrayListExtra(ActivitySignup2.KEY_LABEL,  ArrayList(selectedCategoryList))

                }
                startActivity(intent)
                finish()
            }
        }
    }

}
