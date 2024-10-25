package com.team22.soundary.feature.signup.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.feature.signup.domain.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(){
    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginUiState : StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun login(kakaoToken: String){
        _loginUiState.value = LoginUiState.Loading
        viewModelScope.launch {
            loginUseCase.invoke(kakaoToken).collect{result ->
                result.onSuccess {
                    _loginUiState.value = LoginUiState.Success
                }.onFailure {
                    _loginUiState.value = LoginUiState.Error(it.message)
                }
            }
        }
    }
}