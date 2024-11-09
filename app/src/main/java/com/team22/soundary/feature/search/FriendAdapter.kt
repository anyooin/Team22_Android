package com.team22.soundary.feature.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.FriendItemBasicBinding

class FriendAdapter(
    private val onItemClick: (User) -> Unit,
    private val onDeleteClick: ((User) -> Unit)? = null
) : ListAdapter<User, BasicFriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicFriendViewHolder {
        val binding = FriendItemBasicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasicFriendViewHolder(binding, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: BasicFriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BasicFriendViewHolder(
    private val binding: FriendItemBasicBinding,
    private val onItemClick: (User) -> Unit,
    private val onDeleteClick: ((User) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private var currentFriend: User? = null

    init {
        binding.friendDeleteButton.setOnClickListener {
            currentFriend?.let { friend ->
                onDeleteClick?.invoke(friend)
            }
        }

        binding.root.setOnClickListener {
            currentFriend?.let { friend ->
                onItemClick(friend)
            }
        }
    }

    fun bind(friend: User) {
        currentFriend = friend
        binding.userNameTextview.text = friend.name
        binding.profileInitialTextview.text = friend.name.first().toString()

        val firstGenre = friend.label.firstOrNull() ?: "장르 없음"
        binding.favoriteGenreTextview.text = firstGenre

        binding.userIdTextview.text = "@${friend.displayId}"
    }
}

