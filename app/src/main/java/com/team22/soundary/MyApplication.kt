package com.team22.soundary

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this,NATIVE_KEY)
    }
}