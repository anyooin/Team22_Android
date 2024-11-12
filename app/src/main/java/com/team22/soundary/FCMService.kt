package com.team22.soundary

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.team22.soundary.feature.profile.MyWidgetProvider


class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        // notification 대신 data에서 메시지 가져오기
        super.onMessageReceived(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림을 클릭했을 때 열릴 Activity 설정
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.all_logo_image)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, builder.build())

        //Log.d("uin", "알림왔음" + message.data)

        val code = message.data["code"]
        if(code == MUSIC_SENT_CODE){
            Log.d("uin", "알림왔음22" + message.data["body"])
            updateWidget(message.data["body"] ?: "Error")
        }

        Log.d("uin", "12345")
    }
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // 새로운 토큰을 서버로 전송하는 로직을 구현
//        CoroutineScope(Dispatchers.IO).launch {
//            profileRepository.setDeviceToken(token)
//        }
    }

    private fun createNotificationChannel(): NotificationChannel {
        //val descriptionText = getString(R.string.fcm_channel_description)
        val descriptionText = "채널 설명"
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = descriptionText
        }
        return channel
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

    companion object{
        private const val MUSIC_SENT_CODE = "N0001"

        private const val CHANNEL_ID = "main_default_channel"
        private const val CHANNEL_NAME = "main channelName"
    }

}