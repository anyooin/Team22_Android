package com.team22.soundary.feature.share.presentation.share

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.BottomSheetBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ShareBottomSheet : BottomSheetDialogFragment(R.layout.bottom_sheet) {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BottomSheetAdapter
    private val viewModel: ShareViewModel by activityViewModels()

    private var lastCheckedRadioButtonId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        _binding = BottomSheetBinding.bind(view)

        when (this.tag) {
            MAIN_BOTTOM_SHEET -> {
                setMainSendButton(arguments?.getString(KEY_ID) ?: "")
            }

            SHARE_BOTTOM_SHEET -> {
                setShareSendButton()
                setComment()
            }
        }

        setRecyclerView(view)
        setSelectAllButton()
        setCategoryRadioButton()
        observeSelectedFriends()
        observeFilteredFriends()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireActivity(), R.style.bottomSheetBackground)
    }

    private fun setMainSendButton(trackId: String) {
        binding.bottomSheetSendButton.setOnClickListener {
            if (viewModel.isAnyFriendSelected()) {
                viewModel.setComment(binding.shareCommentEdittext.text.toString())
                viewModel.shareSongToFriends("", trackId)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "친구를 1명 이상 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setShareSendButton() {
        binding.bottomSheetSendButton.setOnClickListener {
            viewModel.setComment(binding.shareCommentEdittext.text.toString())
            viewModel.getFilteredFriendList(null)
            dismiss()
        }
    }

    private fun setComment() {
        binding.shareCommentEdittext.setText(viewModel.comment.value)
    }

    private fun setRecyclerView(view: View) {
        adapter = BottomSheetAdapter(requireContext(), object : FriendItemClickListener {
            override fun onClick(v: View, selectItem: User) {
                viewModel.toggleFriendSelection(selectItem.id)
            }
        })
        binding.selectFriendRecyclerview.adapter = adapter
        binding.selectFriendRecyclerview.layoutManager = GridLayoutManager(view.context, 4)

        lifecycleScope.launch {
            viewModel.filteredUserList.collect { friends ->
                adapter.submitList(friends)
            }
        }
    }

    private fun setSelectAllButton() {
        binding.shareSelectAllButton.setOnClickListener {
            viewModel.setAllFriendsSelected(binding.shareSelectAllButton.isChecked)
        }
    }

    private fun setCategoryRadioButton() {
        binding.categoryRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != -1) {
                val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
                selectedRadioButton.setOnClickListener {
                    if (lastCheckedRadioButtonId == checkedId) {
                        binding.categoryRadioGroup.clearCheck()
                        lastCheckedRadioButtonId = null
                        viewModel.getFilteredFriendList(null)
                    } else {
                        lastCheckedRadioButtonId = checkedId
                        viewModel.getFilteredFriendList(selectedRadioButton.text.toString())
                    }
                }
            }
        }
    }

    private fun observeSelectedFriends() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedFriendIds.collectLatest { selectedIds ->
                updateSendButtonText()
                binding.shareSelectAllButton.isChecked = viewModel.isAllFriendsSelected()
                adapter.setSelectedIds(selectedIds)
            }
        }
    }

    private fun observeFilteredFriends() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredUserList.collectLatest {
                binding.shareSelectAllButton.isChecked = viewModel.isAllFriendsSelected()
            }
        }
    }

    private fun updateSendButtonText() {
        binding.bottomSheetSendButton.text = viewModel.getButtonText()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MAIN_BOTTOM_SHEET = "MainBottomSheet"
        const val SHARE_BOTTOM_SHEET = "ShareBottomSheet"

        private const val KEY_ID = "id"

        fun newInstance(songId: String): ShareBottomSheet {
            val fragment = ShareBottomSheet()
            val args = Bundle()
            args.putString(KEY_ID, songId)
            fragment.arguments = args
            return fragment
        }
    }
}