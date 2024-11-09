package com.team22.soundary.feature.search.presentation.friend

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.FriendDiffCallback

class PendingFriendAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, PendingFriendAdapter.PendingFriendViewHolder>(FriendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingFriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.friend_item_pending, parent, false)
        return PendingFriendViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PendingFriendViewHolder, position: Int) {
        val friend = getItem(position)
        holder.bind(friend)
    }

    class PendingFriendViewHolder(
        itemView: View,
        private val onItemClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val profileInitialTextView: TextView = itemView.findViewById(R.id.profile_initial_textview)
        private val userNameTextView: TextView = itemView.findViewById(R.id.user_name_textview)

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
            profileInitialTextView.text = friend.name.first().toString()
            userNameTextView.text = friend.name
        }
    }
}
