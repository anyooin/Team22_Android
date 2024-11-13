package com.team22.soundary.feature.search.presentation.friend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.ItemSearchResultBinding
import com.team22.soundary.feature.search.FriendDiffCallback

class SearchResultAdapter(
    private val context: Context,
    private val onRequestFriendClick: (User) -> Unit,
    private val isFriendRequested: (User) -> Boolean,
    private val isFriendWithMe: (User) -> Boolean
) : ListAdapter<User, SearchResultAdapter.SearchResultViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchResultViewHolder(
        private val context: Context,
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: User) {
            binding.userNameTextview.text = friend.name
            binding.userIdTextview.text = "@${friend.displayId}"
            binding.favoriteGenreTextview.text = friend.label.joinToString(", ")

            if(friend.imageId != "") {
                binding.profileInitialImageview.visibility = View.VISIBLE
                binding.profileInitialTextview.visibility = View.INVISIBLE
                Glide.with(context)
                    .load(friend.imageId)
                    .circleCrop()
                    .into(binding.profileInitialImageview)
            } else {
                binding.profileInitialImageview.visibility = View.INVISIBLE
                binding.profileInitialTextview.visibility = View.VISIBLE
                binding.profileInitialTextview.text = friend.name[0].toString()
            }

            // 버튼 상태 초기화
            if(isFriendWithMe(friend)) {
                binding.friendRequestButton.text = "친구"
                binding.friendRequestButton.isEnabled = false
            } else if (isFriendRequested(friend)) {
                binding.friendRequestButton.text = "요청됨"
                binding.friendRequestButton.isEnabled = false
            } else {
                binding.friendRequestButton.text = "신청"
                binding.friendRequestButton.isEnabled = true
                binding.friendRequestButton.setOnClickListener {
                    onRequestFriendClick(friend)
                    // 버튼 상태 업데이트
                    binding.friendRequestButton.text = "요청됨"
                    binding.friendRequestButton.isEnabled = false
                }
            }
        }
    }
}
