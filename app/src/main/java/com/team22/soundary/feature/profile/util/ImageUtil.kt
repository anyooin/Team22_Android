package com.team22.soundary.feature.profile.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class ImageUtil {

    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.getWidth(),
            bitmap.getHeight(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.getWidth(), bitmap.getHeight())
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        canvas.drawCircle(
            (bitmap.getWidth() / 2).toFloat(), (bitmap.getHeight() / 2).toFloat(),
            (
                    bitmap.getWidth() / 2).toFloat(), paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val url = URL(uri.toString())
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            getCircularBitmap(BitmapFactory.decodeStream(input))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getBitmapFromStringUrl(stringUrl: String): Bitmap? {
        return if (stringUrl == "") null
        else try {
            val url = URL(stringUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            getCircularBitmap(BitmapFactory.decodeStream(input))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}