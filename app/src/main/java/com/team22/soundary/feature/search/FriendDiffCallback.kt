package com.team22.soundary.feature.search

import androidx.recyclerview.widget.DiffUtil
import com.team22.soundary.core.domain.model.User

class FriendDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
