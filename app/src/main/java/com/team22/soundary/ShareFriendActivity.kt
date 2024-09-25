package com.team22.soundary;

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShareFriendActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_friend)

        // back Button
        val backButton = findViewById<ImageView>(R.id.share_friend_back_button)

        backButton.setOnClickListener {
            val intent = Intent(this, ShareMusicActivity::class.java)
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        // musicInfo
        val musicTextView = findViewById<TextView>(R.id.share_music_textview)
        val singerTextView = findViewById<TextView>(R.id.share_singer_textview)

        val music: String? = intent.extras?.getString("music")
        val singer: String? = intent.extras?.getString("singer")

        musicTextView.text = music
        singerTextView.text = singer

        //recyclerView
        val friendList = mutableListOf<FriendItemEntity>()

        for (i in 0..8) {
            friendList.add(FriendItemEntity("이름"))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.share_friend_recyclerview)

        recyclerView.adapter = FriendListAdapter(friendList, LayoutInflater.from(this))
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // bottom Sheet
        val addFriendButton = findViewById<ImageView>(R.id.share_add_friend)

        addFriendButton.setOnClickListener {
            val modal = ShareBottomSheet()
            modal.show(supportFragmentManager, ShareBottomSheet.TAG)
        }

    }
}

class FriendItemEntity(
    val name: String
)

class FriendListAdapter(
    private var friendItemList: List<FriendItemEntity>,
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView

        init {
            nameTextView = itemView.findViewById<TextView>(R.id.share_friend_textview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.share_friend_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTextView.text = friendItemList[position].name
    }

    override fun getItemCount(): Int {
        return friendItemList.size
    }
}
