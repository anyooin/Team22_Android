package com.team22.soundary.feature.signup.presentation

sealed class LoginUiState<out T> {
    data object Initial : LoginUiState<Nothing>()
    data object Loading : LoginUiState<Nothing>()
    data class Success<out T>(val data: T) : LoginUiState<T>()
    data object Pass : LoginUiState<Nothing>()
    data class Error(val message: String?) : LoginUiState<Nothing>()
}
