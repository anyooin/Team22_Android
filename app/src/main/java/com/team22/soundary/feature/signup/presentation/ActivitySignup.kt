package com.team22.soundary.feature.signup.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.user.UserApiClient
import com.team22.soundary.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import com.team22.soundary.R
import android.widget.Button
import android.widget.ScrollView

@AndroidEntryPoint
class ActivitySignup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val selectedCategoryList = mutableSetOf<String>()

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
        setupTermsCheckboxes()
    }

    private var isPrivacyPopupShown = false
    private var isServicePopupShown = false

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

    private fun loadRawResourceText(resourceId: Int): String {
        val inputStream = resources.openRawResource(resourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun setupTermsCheckboxes() {
        binding.signupCheckboxPrivacy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !isPrivacyPopupShown) {
                isPrivacyPopupShown = true
                val privacyPolicyText = loadRawResourceText(R.raw.privacy_policy)
                showTermsPopup(getString(R.string.privacy_policy_title), privacyPolicyText)
            } else if (!isChecked) {
                isPrivacyPopupShown = false
            }
        }

        binding.signupCheckboxService.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !isServicePopupShown) {
                isServicePopupShown = true
                val servicePolicyText = loadRawResourceText(R.raw.service_policy)
                showTermsPopup(getString(R.string.service_policy_title), servicePolicyText)
            } else if (!isChecked) {
                isServicePopupShown = false
            }
        }
    }

    private fun showTermsPopup(title: String, content: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_terms, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // 팝업이 확인 버튼을 누르기 전에는 닫히지 않음
            .create()

        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val contentTextView = dialogView.findViewById<TextView>(R.id.dialogContent)
        val closeButton = dialogView.findViewById<Button>(R.id.closeButton)
        val scrollView = dialogView.findViewById<ScrollView>(R.id.scrollView)

        titleTextView.text = title
        contentTextView.text = content
        closeButton.isEnabled = false // 스크롤을 끝까지 내리기 전까지 비활성화

        // 스크롤이 끝까지 내려가면 "확인" 버튼 활성화
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.getChildAt(0).bottom <= (scrollView.height + scrollView.scrollY)) {
                closeButton.isEnabled = true
            }
        }

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun setNextSignupButton() {
        binding.signupButtonContinue.setOnClickListener {
            val nickname = binding.signupEdittextNickname.text.toString()
            val displayId = binding.signupEdittextEmail.text.toString()
            val isPrivacyChecked = binding.signupCheckboxPrivacy.isChecked
            val isServiceChecked = binding.signupCheckboxService.isChecked


            when {
                nickname.isEmpty() -> {
                    Toast.makeText(this, "닉네임은 필수 입력칸입니다.", Toast.LENGTH_SHORT).show()
                }

                selectedCategoryList.isEmpty() -> {
                    Toast.makeText(this, "카테고리를 하나 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
                }

                !isPrivacyChecked -> {
                    Toast.makeText(this, "개인정보 수집 및 이용 동의를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }

                !isServiceChecked -> {
                    Toast.makeText(this, "서비스 이용 약관 동의를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    val intent = Intent(this, ActivitySignup2::class.java).apply {
                        this.putExtra(ActivitySignup2.KEY_NICKNAME, nickname)
                        this.putExtra(ActivitySignup2.KEY_DISPLAY_ID, displayId)
                        this.putStringArrayListExtra(
                            ActivitySignup2.KEY_LABEL,
                            ArrayList(selectedCategoryList)
                        )

                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
