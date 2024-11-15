package com.team22.soundary.feature.share.presentation.share

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.databinding.ShareFriendItemNoImageBinding
import com.team22.soundary.databinding.ShareFriendItemWithImageBinding

class BottomSheetAdapter(
    private val context: Context,
    private val listener: FriendItemClickListener
) : ListAdapter<User, RecyclerView.ViewHolder>(FriendItemDiffCallback()) {

    private var selectedIds: Set<String> = emptySet()

    abstract class BaseViewHolder(
        view: View,
        private val listener: FriendItemClickListener
    ) : RecyclerView.ViewHolder(view) {
        abstract fun bind(userItem: User, isSelected: Boolean)

        protected fun setClickListener(item: User) {
            itemView.setOnClickListener {
                listener.onClick(it, item)
            }
        }
    }

    class ViewHolderNoImage(
        private val binding: ShareFriendItemNoImageBinding,
        listener: FriendItemClickListener
    ) : BaseViewHolder(binding.root, listener) {
        override fun bind(userItem: User, isSelected: Boolean) {
            binding.shareFriendImage.text = userItem.name[0].toString()
            binding.shareFriendTextview.text = userItem.name
            binding.shareGrayBackground.visibility =
                if (isSelected) View.VISIBLE else View.INVISIBLE
            setClickListener(userItem)
        }
    }

    class ViewHolderWithImage(
        private val context: Context,
        private val binding: ShareFriendItemWithImageBinding,
        listener: FriendItemClickListener
    ) : BaseViewHolder(binding.root, listener) {
        override fun bind(userItem: User, isSelected: Boolean) {
            Glide.with(context)
                .load(userItem.imageId)
                .into(binding.shareFriendImage)
            binding.shareFriendTextview.text = userItem.name
            binding.shareGrayBackground.visibility =
                if (isSelected) View.VISIBLE else View.INVISIBLE
            setClickListener(userItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            NO_IMAGE -> {
                val binding = ShareFriendItemNoImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolderNoImage(binding, listener)
            }

            WITH_IMAGE -> {
                val binding = ShareFriendItemWithImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ViewHolderWithImage(context, binding, listener)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is BaseViewHolder -> holder.bind(item, selectedIds.contains(item.id))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).imageId == "") NO_IMAGE else WITH_IMAGE
    }


    fun setSelectedIds(ids: Set<String>) {
        selectedIds = ids
        notifyDataSetChanged()
    }

    companion object {
        const val NO_IMAGE = 0
        const val WITH_IMAGE = 1
    }
}

interface FriendItemClickListener {
    fun onClick(v: View, selectItem: User)
}