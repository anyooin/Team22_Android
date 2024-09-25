package com.team22.soundary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShareBottomSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.bottom_sheet_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<TextView>(R.id.bottom_sheet_send_button)

        button.setOnClickListener {
            dismiss()
        }

        //recyclerView
        val friendList = mutableListOf<FriendItemEntity>()

        for (i in 0..10) {
            friendList.add(FriendItemEntity("이름"))
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.select_friend_recyclerview)

        recyclerView.adapter = BottomSheetAdapter(friendList)
        //recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = GridLayoutManager(view.context, 4)
    }

    companion object {
        const val TAG = "ShareBottomModalSheet"
    }

}

class BottomSheetAdapter(
    private var friendItemList: List<FriendItemEntity>,
) : RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>() {
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView

        init {
            nameTextView = itemView.findViewById<TextView>(R.id.share_friend_textview)
        }
//        fun bind(item: BottomDialogItem) {
//            view.findViewById<TextView>(R.id.textView).text = item.name
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.share_friend_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = friendItemList[position].name
//        val item = friendItemList[position]
//        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return friendItemList.size
    }
}
