package com.team22.soundary;

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShareMusicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_music)

        // spinner
        val spinner = findViewById<Spinner>(R.id.share_sort_spinner)
        spinner.adapter = ArrayAdapter(
            this,
            R.layout.share_spinner_item,
            resources.getStringArray(R.array.share_mock_array)
        )


        // recyclerView
        val musicList = mutableListOf<MusicItemEntity>()

        for (i in 0..5) {
            musicList.add(MusicItemEntity("노래이름1", "가수", "100명이 공유함"))
            musicList.add(MusicItemEntity("노래이름2", "가수", "100명이 공유함"))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.share_music_recyclerview)

        val mapListAdapter = MusicListAdapter(musicList, LayoutInflater.from(this))

        recyclerView.adapter = mapListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mapListAdapter.setItemClickListener(object : ItemClickListener {
            override fun onClick(v: View, musicItem: MusicItemEntity) {
                val intent = Intent(this@ShareMusicActivity, ShareFriendActivity::class.java)
                intent.putExtra("music", musicItem.music)
                intent.putExtra("singer", musicItem.singer)
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })
    }
}

class MusicItemEntity(
    val music: String,
    val singer: String,
    val sortValue: String
)

class MusicListAdapter(
    private var musicItemList: List<MusicItemEntity>,
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val musicTextView: TextView
        val singerTextView: TextView
        val sortTextView: TextView

        init {
            musicTextView = itemView.findViewById<TextView>(R.id.share_music_textview)
            singerTextView = itemView.findViewById<TextView>(R.id.share_singer_textview)
            sortTextView = itemView.findViewById<TextView>(R.id.share_sort_textview)
            itemView.setOnClickListener {
                itemListener.onClick(it, musicItemList[adapterPosition])
            }
        }
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemListener = itemClickListener
    }

    lateinit var itemListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.share_music_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.musicTextView.text = musicItemList[position].music
        holder.singerTextView.text = musicItemList[position].singer
        holder.sortTextView.text = musicItemList[position].sortValue
    }

    override fun getItemCount(): Int {
        return musicItemList.size
    }
}


interface ItemClickListener {
    fun onClick(v: View, selectItem: MusicItemEntity)
}