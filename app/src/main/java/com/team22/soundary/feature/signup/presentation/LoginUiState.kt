package com.team22.soundary.feature.signup.presentation

sealed class LoginUiState {
    data object Initial : LoginUiState()
    data object Loading : LoginUiState()
    data object Success : LoginUiState()
    data class Error(val message: String?) : LoginUiState()
}