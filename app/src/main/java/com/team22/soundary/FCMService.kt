package com.team22.soundary

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.Constants.MessageNotificationKeys
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.team22.soundary.extensions.checkAndRequestPermissions
import com.team22.soundary.feature.profile.MyWidgetProvider
import com.team22.soundary.feature.signup.presentation.ActivitySignup2


class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        // notification 대신 data에서 메시지 가져오기
        super.onMessageReceived(message)
        Log.d("widgetkk", "message data: ${message.data}")

        // 알림을 클릭했을 때 열릴 Activity 설정
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        var builder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.all_logo_image)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)


        if(this.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            with(NotificationManagerCompat.from(this)) {
                notify(0, builder.build())
            }
        }

        val code = message.data["code"]
        if(code == "N0001"){
            updateWidget(message.data["body"] ?: "Error")
        }
    }


    private fun updateWidget(msg: String){
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = ComponentName(this,MyWidgetProvider::class.java)

        val intent = Intent(this, MyWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                appWidgetManager.getAppWidgetIds(appWidgetIds))
            putExtra("message", msg)
        }

        sendBroadcast(intent)
    }

//    override fun handleIntent(intent: Intent?) {
//        try {
//            // notification 관련 키만 제거하고 data는 유지
//            intent?.extras?.keySet()?.forEach { key ->
//                if (key.startsWith("google.c.n.")) {
//                    intent.removeExtra(key)
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("FCM", "Error cleaning notification data", e)
//        }
//
//        super.handleIntent(intent)
//    }

}