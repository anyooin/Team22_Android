package com.team22.soundary.feature.signup.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kakao.sdk.user.UserApiClient
import com.team22.soundary.MainActivity
import com.team22.soundary.databinding.ActivitySignIntroBinding
import com.team22.soundary.util.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivitySignIntro : AppCompatActivity() {

    private lateinit var binding: ActivitySignIntroBinding
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel.checkTokenValidity()

        binding = ActivitySignIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            handleLogin()
        }
        observeState()
    }

    private fun handleLogin() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                showErrorDialog(error.message ?: UNKNOWN_ERROR)
            } else if (token != null) {
                viewModel.login(token.accessToken)
                Log.d("accessToken:", token.accessToken)
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.loginUiState.collect { state ->
                when (state) {
                    is LoginUiState.Initial -> {}
                    is LoginUiState.Success -> {
                        val data = state.data
                        //val intent = Intent(this@ActivitySignIntro,ActivitySignup::class.java)
                        Log.d("testt", "" + data.role)
                        val intent = if (REGISTERED_USER_ROLE in data.role) {
                            Intent(this@ActivitySignIntro, MainActivity::class.java)
                        } else {
                            Intent(this@ActivitySignIntro, ActivitySignup::class.java)
                        }

                        startActivity(intent)
                        finish()
                    }

                    is LoginUiState.Loading -> {
                        LoadingDialog(this@ActivitySignIntro).show()
                    }

                    is LoginUiState.Error -> {
                        showErrorDialog(state.message ?: UNKNOWN_ERROR)
                    }

                    is LoginUiState.Pass -> {
                        startActivity(
                            Intent(this@ActivitySignIntro, MainActivity::class.java)
                        )
                        finish()
                    }
                }
            }
        }
    }

    private fun showErrorDialog(message: String) =
        MaterialAlertDialogBuilder(this@ActivitySignIntro)
            .setMessage("로그인에 실패하였습니다.\n에러: $message")
            .setCancelable(false)
            .setNeutralButton("재시도") { _, _ ->
                handleLogin()
            }
            .show()

    companion object {
        const val UNKNOWN_ERROR = "unknown error"
        const val REGISTERED_USER_ROLE = "USER"
    }
}