package com.team22.soundary.feature.main.presentation

import com.team22.soundary.R
import com.team22.soundary.core.domain.model.Share
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()

    data class Error(val message: String?) : UiState<Nothing>()
}

data class MainUiState(
    val share: Share = Share(),
    val friendNameList: List<String> = emptyList(),
    val isLastSong: Boolean = true,
    val isFirstSong: Boolean = true,
    val likeBackground: Int = R.drawable.main_like_background
)
