package com.team22.soundary.feature.profile

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.team22.soundary.R
import com.team22.soundary.feature.main.data.ReceivedShareRepositoryImpl
import com.team22.soundary.feature.main.domain.ReceivedShareRepository
import com.team22.soundary.feature.profile.util.ImageUtil
import com.team22.soundary.feature.signup.presentation.ActivitySignIntro
import com.team22.soundary.feature.signup.presentation.ActivitySignup
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

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
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent != null && intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(
                AppWidgetManager.EXTRA_APPWIDGET_IDS
            )

            CoroutineScope(Dispatchers.IO).launch {
                val receivedShareRepository: ReceivedShareRepository by lazy {
                    EntryPointAccessors.fromApplication(
                        context!!.applicationContext,
                        RepositoryEntryPoint::class.java
                    ).receivedShareRepository()
                }

                val receivedShare = runBlocking {
                    receivedShareRepository?.getShareList()?.firstOrNull()
                }

                val bitmap =
                    ImageUtil().getBitmapFromUri(context!!,receivedShare?.first()?.song?.coverImage ?: Uri.EMPTY)

                withContext(Dispatchers.Main) {
                    // 위젯 업데이트
                    appWidgetIds?.forEach { appWidgetId ->
                        val views = RemoteViews(
                            context?.packageName,
                            R.layout.widget_main
                        ).apply {

                            this.setImageViewBitmap(R.id.profile_frame, bitmap)
                            this.setTextViewText(
                                R.id.text_circle_background,
                                receivedShare?.first()?.friend?.name?.get(0)?.toString() ?: ""
                            )
                        }

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }
    }



    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_main)

        val intent = Intent(context, ActivitySignIntro::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.widget, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

