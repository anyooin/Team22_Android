package com.team22.soundary.feature.main.presentation

import android.net.Uri
import com.team22.soundary.R
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.feature.signup.presentation.LoginUiState

data class MainUiState(
    val share : Share = Share(),
    val friendNameList: List<String> = emptyList(),
    val isLastSong: Boolean = true,
    val isFirstSong: Boolean = true,
    val likeBackground : Int = 0
)
