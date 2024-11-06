package com.team22.soundary.feature.profile.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.team22.soundary.databinding.FragmentMypageEditBinding
import com.team22.soundary.feature.profile.domain.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileEditedFragment : Fragment() {
    private var _binding: FragmentMypageEditBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    private val selectedCategories = mutableSetOf<String>()

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

        binding.saveButton.setOnClickListener {
            profileViewModel.loadLabels() // 저장 시 라벨 목록 조회
        }
    }

    private fun setupCategoryButtons() {
        // 각 버튼에 대해 클릭 이벤트 설정
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
            profileViewModel.deleteLabel(category) // 라벨 삭제 API 호출
            button.setBackgroundColor(Color.WHITE)  // 선택 해제 시 흰색 배경
            (button as Button).setTextColor(Color.BLACK)
        } else {
            selectedCategories.add(category)
            profileViewModel.addLabel(category) // 라벨 추가 API 호출
            button.setBackgroundColor(Color.parseColor("#800080"))  // 선택 시 보라색 배경
            (button as Button).setTextColor(Color.WHITE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
