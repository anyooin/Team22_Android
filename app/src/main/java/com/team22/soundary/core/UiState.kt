package com.team22.soundary.core

sealed class UiState<out T>(val _data: T?) {
    object Loading : UiState<Nothing>(_data = null)
    data class Success<out R>(val data: R) : UiState<R>(_data = data)

    data class Error(val message: String?) : UiState<Nothing>(_data = null)
}