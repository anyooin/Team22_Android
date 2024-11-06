package com.team22.soundary.feature.signup.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.user.UserApiClient
import com.team22.soundary.core.domain.model.Category
import com.team22.soundary.databinding.ActivitySignupBinding
import com.team22.soundary.feature.profile.fragment.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivitySignup : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val selectedCategories = mutableListOf<Int>()
    private val viewModel: SignupViewModel by viewModels() // ViewModel 초기화

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

        val categoryButtons = listOf(
            binding.signupTogglebuttonCategoryHiphop,
            binding.signupTogglebuttonCategoryRock,
            binding.signupTogglebuttonCategoryPop,
            binding.signupTogglebuttonCategoryJpop,
            binding.signupTogglebuttonCategoryKpop,
            binding.signupTogglebuttonCategoryRnb
        )

        setupCategoryButtons(categoryButtons)

        // 계속 가입하기 버튼 클릭 시 처리
        binding.signupButtonContinue.setOnClickListener {
            val nickname = binding.signupEdittextNickname.text.toString()
            val selectedCategoryNames = selectedCategories.map { getCategoryNameById(it) } // 라벨 이름 목록 생성
            viewModel.saveSelectedCategories(selectedCategoryNames)

            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임은 필수 입력칸입니다.", Toast.LENGTH_SHORT).show()
            } else if (selectedCategories.isEmpty()) {
                Toast.makeText(this, "카테고리를 하나 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ActivitySignup2::class.java).apply {
                    this.putExtra("nickname", nickname)
                    this.putExtra("category", selectedCategories.toIntArray())
                }
                viewModel.saveSelectedCategories(selectedCategoryNames) // 선택한 카테고리 저장
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setEmailId(email: String) {
        val emailId = email.substringBefore("@")
        binding.signupEdittextEmail.setText(emailId)
        binding.signupEdittextEmail.isEnabled = false
    }

    private fun setupCategoryButtons(categoryButtons: List<ToggleButton>) {
        for (button in categoryButtons) {
            button.setOnClickListener {
                if (button.isChecked) {
                    if (selectedCategories.size < 3) {
                        selectedCategories.add(getCategoryByButton(button))
                    } else {
                        button.isChecked = false
                        Toast.makeText(this, "최대 3개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedCategories.remove(getCategoryByButton(button))
                }
            }
        }
    }

    private fun getCategoryByButton(button: ToggleButton): Int {
        return when (button) {
            binding.signupTogglebuttonCategoryHiphop -> Category.HIPHOP.ordinal
            binding.signupTogglebuttonCategoryRock -> Category.ROCK.ordinal
            binding.signupTogglebuttonCategoryPop -> Category.POP.ordinal
            binding.signupTogglebuttonCategoryJpop -> Category.JPOP.ordinal
            binding.signupTogglebuttonCategoryKpop -> Category.KPOP.ordinal
            binding.signupTogglebuttonCategoryRnb -> Category.RNB.ordinal
            else -> -1
        }
    }

    private fun getCategoryNameById(id: Int): String {
        return when (id) {
            binding.signupTogglebuttonCategoryHiphop.id -> "힙합"
            binding.signupTogglebuttonCategoryRock.id -> "ROCK"
            binding.signupTogglebuttonCategoryPop.id -> "POP"
            binding.signupTogglebuttonCategoryJpop.id -> "JPOP"
            binding.signupTogglebuttonCategoryKpop.id -> "KPOP"
            binding.signupTogglebuttonCategoryRnb.id -> "RNB"
            else -> ""
        }
    }
}
