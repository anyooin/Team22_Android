package com.team22.soundary.feature.signup.presentation

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.kakao.sdk.user.UserApiClient
import com.team22.soundary.databinding.ActivitySignIntroBinding
import com.team22.soundary.util.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActivitySignIntro : AppCompatActivity() {

    private lateinit var binding: ActivitySignIntroBinding
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            handleLogin()
        }
        observeState()
    }

    private fun handleLogin(){
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                showErrorDialog(error.message ?: UNKNOWN_ERROR)
            } else if(token != null){
                viewModel.login(token.accessToken)
            }
        }
    }

    private fun observeState(){
        lifecycleScope.launch {
            viewModel.loginUiState.collect{ state ->
                when(state){
                    is LoginUiState.Initial -> {}
                    is LoginUiState.Success -> {
                        startActivity(
                            Intent(this@ActivitySignIntro, ActivitySignup::class.java)
                        )
                    }
                    is LoginUiState.Loading -> {
                        LoadingDialog(this@ActivitySignIntro).show()
                    }
                    is LoginUiState.Error -> {
                        showErrorDialog(state.message ?: UNKNOWN_ERROR)
                    }
                }
            }
        }
    }

    private fun showErrorDialog(message: String) =
        MaterialAlertDialogBuilder(this@ActivitySignIntro)
            .setMessage("로그인에 실패하였습니다.\n에러: $message")
            .setCancelable(false)
            .setNeutralButton("재시도") { dialog, which ->
                handleLogin()
            }
            .show()

    companion object{
        const val UNKNOWN_ERROR = "unknown error"
    }
}