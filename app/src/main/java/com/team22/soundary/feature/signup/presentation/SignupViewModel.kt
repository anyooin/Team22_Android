package com.team22.soundary.feature.signup.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.signup.domain.CheckTokenUseCase
import com.team22.soundary.feature.signup.domain.LoginUseCase
import com.team22.soundary.feature.signup.domain.UserDetailUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userDetailUpdateUseCase: UserDetailUpdateUseCase,
    private val checkTokenUseCase: CheckTokenUseCase
) : ViewModel() {
    private val _loginUiState = MutableStateFlow<LoginUiState<User>>(LoginUiState.Initial)
    val loginUiState: StateFlow<LoginUiState<User>> = _loginUiState.asStateFlow()

    private val _imageId = MutableStateFlow<String>("")
    val imageId: StateFlow<String> = _imageId.asStateFlow()

    fun login(kakaoToken: String) {
        _loginUiState.value = LoginUiState.Loading
        viewModelScope.launch {
            loginUseCase.invoke(kakaoToken)
                .catch { e ->
                    val errorMessage = when (e) {
                        is IOException -> e.message
                        is IllegalStateException -> e.message
                        else -> e.message
                    }
                    _loginUiState.value = LoginUiState.Error(errorMessage)
                }
                .collect { result ->
                    _loginUiState.value = LoginUiState.Success(
                        result
                    )
                }
        }
    }

    fun uploadImage(multipart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                _imageId.value = userDetailUpdateUseCase.uploadImage(multipart)
            } catch (e: Exception) {
                Log.e("uin", "Error uploading image: ${e.message}")
            }
        }
    }

    suspend fun updateUserInfo(token: String, user: User): Boolean = withContext(Dispatchers.IO) {
        return@withContext userDetailUpdateUseCase.updateUserInfo(token, user)
    }

    fun checkTokenValidity() {
        viewModelScope.launch {
            try {
                checkTokenUseCase.invoke()
                _loginUiState.value = LoginUiState.Pass
            } catch (e: Exception) {
                _loginUiState.value = LoginUiState.Initial
            }
        }
    }
}