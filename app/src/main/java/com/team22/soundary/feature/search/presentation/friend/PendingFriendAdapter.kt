package com.team22.soundary.feature.search.presentation.friend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.FriendItemPendingBinding
import com.team22.soundary.feature.search.FriendDiffCallback

class PendingFriendAdapter(
    private val context: Context,
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, PendingFriendAdapter.PendingFriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingFriendViewHolder {
        val binding =
            FriendItemPendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingFriendViewHolder(context, binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PendingFriendViewHolder, position: Int) {
        val friend = getItem(position)
        holder.bind(friend)
    }

    class PendingFriendViewHolder(
        private val context: Context,
        private val binding: FriendItemPendingBinding,
        private val onItemClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentFriend: User? = null

        init {
            // 클릭 리스너 설정
            itemView.setOnClickListener {
                currentFriend?.let { friend ->
                    onItemClick(friend)
                }
            }
        }

        fun bind(friend: User) {
            currentFriend = friend // 현재 friend 객체를 저장하여 클릭 리스너에서 사용
            if (friend.imageId != "") {
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
            binding.userNameTextview.text = friend.name
        }
    }
}
