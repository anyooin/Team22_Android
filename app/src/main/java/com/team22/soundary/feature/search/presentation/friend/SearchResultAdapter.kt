package com.team22.soundary.feature.search.presentation.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.ItemSearchResultBinding
import com.team22.soundary.feature.search.FriendDiffCallback

class SearchResultAdapter(
    private val onRequestFriendClick: (User) -> Unit,
    private val isFriendRequested: (User) -> Boolean
) : ListAdapter<User, SearchResultAdapter.SearchResultViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.userNameTextview.text = user.name
            binding.userIdTextview.text = user.id
            binding.profileInitialTextview.text = user.name.first().toString()

            // 버튼 상태 초기화
            if (isFriendRequested(user)) {
                binding.friendRequestButton.text = "요청됨"
                binding.friendRequestButton.isEnabled = false
            } else {
                binding.friendRequestButton.text = "신청"
                binding.friendRequestButton.isEnabled = true
                binding.friendRequestButton.setOnClickListener {
                    onRequestFriendClick(user)
                    // 버튼 상태 업데이트
                    binding.friendRequestButton.text = "요청됨"
                    binding.friendRequestButton.isEnabled = false
                }
            }
        }
    }
}
