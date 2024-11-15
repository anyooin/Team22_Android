package com.team22.soundary.feature.search.presentation.friend

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.FriendItemNewBinding
import com.team22.soundary.feature.search.FriendDiffCallback

class NewFriendAdapter(
    private val context: Context,
    private val onItemClick: (User) -> Unit,
    private val onAcceptClick: ((User) -> Unit)? = null,
    private val onDeclineClick: ((User) -> Unit)? = null
) : ListAdapter<User, NewFriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFriendViewHolder {
        val binding =
            FriendItemNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewFriendViewHolder(context, binding, onItemClick, onAcceptClick, onDeclineClick)
    }

    override fun onBindViewHolder(holder: NewFriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class NewFriendViewHolder(
    private val context: Context,
    private val binding: FriendItemNewBinding,
    private val onItemClick: (User) -> Unit,
    private val onAcceptClick: ((User) -> Unit)?,
    private val onDeclineClick: ((User) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private var currentFriend: User? = null

    init {
        binding.friendAcceptButton.setOnClickListener {
            currentFriend?.let { friend ->
                onAcceptClick?.invoke(friend)
            }
        }

        binding.friendDeclineButton.setOnClickListener {
            currentFriend?.let { friend ->
                onDeclineClick?.invoke(friend)
                Log.d("NewFriendViewHolder", "Decline clicked for user: ${friend.displayId}")
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

        binding.userIdTextview.text = "@${friend.displayId}"
    }
}
