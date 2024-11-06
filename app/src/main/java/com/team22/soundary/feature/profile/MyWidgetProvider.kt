package com.team22.soundary.feature.profile

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.team22.soundary.R
import com.team22.soundary.feature.profile.util.ImageUtil

class MyWidgetProvider : AppWidgetProvider() {

    @SuppressLint("RemoteViewLayout")
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
//        for (appWidgetId in appWidgetIds) {
//            val views = RemoteViews(context.packageName, R.layout.widget_main)
//            val nickname = "쿠키즈"
//            views.setTextViewText(R.id.text_circle_background, nickname.first().toString())
//
//            val originalBitmap =
//                BitmapFactory.decodeResource(context.resources, R.drawable.widget_image)
//            val circularBitmap: Bitmap = ImageUtil().getCircularBitmap(originalBitmap)
//
//            views.setImageViewBitmap(R.id.profile_frame,circularBitmap)
//
//            val hasNotification = true // 알림이 있다고 가정
//            if (hasNotification) {
//                views.setViewVisibility(R.id.widget_notification_dot, View.VISIBLE)
//            } else {
//                views.setViewVisibility(R.id.widget_notification_dot, View.GONE)
//            }
//
//            //위젯 업데이트
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if(intent != null && intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE){
            val message = intent.getStringExtra("message")

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS
            )

            Log.d("widgetkk","in Provider : "+message)

            // 위젯 업데이트
            appWidgetIds?.forEach { appWidgetId ->
                val views = RemoteViews(
                    context?.packageName,
                    R.layout.widget_main
                ).apply {
                    message?.let{
                        val originalBitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.widget_image)
                        val circularBitmap: Bitmap = ImageUtil().getCircularBitmap(originalBitmap)

                        this.setImageViewBitmap(R.id.profile_frame, circularBitmap)
                        this.setTextViewText(R.id.text_circle_background,it.firstOrNull().toString())
                    }
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_main)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
