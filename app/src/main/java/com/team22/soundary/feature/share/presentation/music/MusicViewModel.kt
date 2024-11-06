package com.team22.soundary.feature.share.presentation.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.domain.model.Song
import com.team22.soundary.feature.share.domain.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {
    private val _songList = MutableStateFlow<List<Song>>(emptyList())
    val songList: StateFlow<List<Song>> = _songList.asStateFlow()

    private var sortIndex: Int = 0

    // 공유한 노래 없으면 emptylist로 떠서 일단 getMusicList로 해놨음
    fun changeSongListBySort(index: Int) {
        sortIndex = index
        viewModelScope.launch {
//            when (sortIndex) {
//                MOST_SHARED -> repository.getMusicList("hi").collect {
//                    _songList.value = it
//                }
//
//                MOST_LIKED -> repository.getMusicList("hello").collect {
//                    _songList.value = it
//                }
//            }
            when (sortIndex) {
                MOST_SHARED -> repository.getMostSharedMusicList().collect {
                    _songList.value = it
                }

                MOST_LIKED -> repository.getMostLikedMusicList().collect {
                    _songList.value = it
                }
            }
        }
    }

    fun changeSongListBySearch(query: String) {
        viewModelScope.launch {
            if (query == "") {
                changeSongListBySort(sortIndex)
            } else {
                repository.getMusicList(query).collect {
                    _songList.value = it
                }
            }
        }
    }

    companion object {
        const val MOST_SHARED = 0
        const val MOST_LIKED = 1
    }
}