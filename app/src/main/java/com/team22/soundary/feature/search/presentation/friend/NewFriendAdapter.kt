package com.team22.soundary.feature.search.presentation.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.FriendItemNewBinding
import com.team22.soundary.feature.search.FriendDiffCallback

class NewFriendAdapter(
    private val onItemClick: (User) -> Unit,
    private val onAcceptClick: ((User) -> Unit)? = null,
    private val onDeclineClick: ((User) -> Unit)? = null
) : ListAdapter<User, NewFriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewFriendViewHolder {
        val binding = FriendItemNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewFriendViewHolder(binding, onItemClick, onAcceptClick, onDeclineClick)
    }

    override fun onBindViewHolder(holder: NewFriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class NewFriendViewHolder(
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

        val firstGenre = friend.category.firstOrNull() ?: "장르 없음"
        binding.favoriteGenreTextview.text = firstGenre.toString()

        binding.userIdTextview.text = "@${friend.id}"
    }
}
