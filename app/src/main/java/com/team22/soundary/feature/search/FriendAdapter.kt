package com.team22.soundary.feature.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.FriendItemBasicBinding

class FriendAdapter(
    private val context: Context,
    private val onItemClick: (User) -> Unit,
    private val onDeleteClick: ((User) -> Unit)? = null
) : ListAdapter<User, BasicFriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicFriendViewHolder {
        val binding =
            FriendItemBasicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasicFriendViewHolder(context, binding, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: BasicFriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BasicFriendViewHolder(
    private val context: Context,
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
        binding.userIdTextview.text = "@${friend.displayId}"
        binding.favoriteGenreTextview.text = friend.label.joinToString(", ")

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
    }
}

