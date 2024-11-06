package com.team22.soundary.feature.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onRequestFriendClick: (User) -> Unit
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
            binding.friendRequestButton.setOnClickListener {
                onRequestFriendClick(user)
            }
        }
    }
}
