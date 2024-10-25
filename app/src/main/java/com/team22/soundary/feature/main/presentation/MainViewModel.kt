package com.team22.soundary.feature.main.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.R
import com.team22.soundary.core.UiState
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.feature.main.domain.GetShareUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getShareUseCase: GetShareUseCase
) : ViewModel() {
    private var _groupedShares: Map<String, List<Share>> = emptyMap()
    private val _uiState = MutableStateFlow<UiState<MainUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<MainUiState>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getShareUseCase.invoke().collect { result ->
                result.onSuccess {
                    _groupedShares = it
                    if (it.isNotEmpty()) updateUiState(it.entries.first().value.first(), 0)
                }.onFailure {exception ->
                    _uiState.update {
                        UiState.Error(
                            exception.message ?: UNKNOWN_ERROR
                        )
                    }
                }
            }
        }
    }


    fun onFriendChanged(index: Int) {
        val targetFriendName = _groupedShares.keys.toList()[index]
        _groupedShares[targetFriendName]?.first()?.let { updateUiState(it, 0) }
    }

    fun onNextClicked() {
        val currentIndex = getCurrentShareIndex()
        val nextIndex = currentIndex + 1
        val currentShares = getCurrentShares()

        if (currentIndex == EMPTY_SHARE || nextIndex == currentShares?.size) return
        currentShares?.let {
            updateUiState(it[nextIndex], nextIndex)
        }
    }

    fun onPrevClicked() {
        val currentIndex = getCurrentShareIndex()
        val prevIndex = currentIndex - 1
        val targetShare = getCurrentShares()

        if (currentIndex == EMPTY_SHARE || prevIndex < 0) return
        targetShare?.let {
            updateUiState(it[prevIndex], prevIndex)
        }
    }

    fun getSongUri(): Uri? = (_uiState.value as? UiState.Success)?.data?.share?.song?.preview

    private fun getCurrentShares(): List<Share>? =
        _groupedShares[(_uiState.value as? UiState.Success)?.data?.share?.friend?.name]

    private fun getCurrentShareIndex(): Int {
        val a = _uiState.value as? UiState.Success
        val shares = getCurrentShares()
        return shares?.indexOfFirst { it.id == a?.data?.share?.id } ?: EMPTY_SHARE
    }

    private fun updateUiState(targetShare: Share, shareIndex: Int) {
        _uiState.update {
            UiState.Success(
                MainUiState(
                    share = targetShare,
                    friendNameList = _groupedShares.keys.toList(),
                    isLastSong = shareIndex == _groupedShares[targetShare.friend.name]?.size?.minus(
                        1
                    ),
                    isFirstSong = shareIndex == 0,
                    likeBackground = if (targetShare.isLike) R.drawable.main_like_background_pressed else R.drawable.main_like_background,
                )
            )
        }
    }

    companion object {
        const val EMPTY_SHARE = -1
        const val UNKNOWN_ERROR = "unknown error"
    }
}