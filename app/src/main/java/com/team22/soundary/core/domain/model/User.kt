package com.team22.soundary.core.domain.model

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.FileOutputStream


data class User(
    val id: String = "",
    val displayId: String = "",
    val name: String = "",
    val email: String = "",
    val imageId: String = "",
    val statusMessage: String = "",
    val label : List<String> = emptyList(),
    var status: String = "",
    val role : List<String> = emptyList()
)

fun createImageMultipart(context: Context, imageUri: Uri?, partName: String = "image"): MultipartBody.Part {
    val file = File(getPathFromUri(context, imageUri!!)) // Uri를 실제 파일 경로로 변환
    Log.d("uin", "파일" + file)
    val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull()) // 파일을 RequestBody로 변환
    Log.d("uin", "파일 : " + MultipartBody.Part.createFormData(partName, file.name, requestFile))
    return MultipartBody.Part.createFormData(partName, file.name, requestFile) // MultipartBody.Part 생성
}

fun createImageMultipart(context: Context, imageUri: Uri, partName: String = "image", quality: Int = 50): MultipartBody.Part? {
    // Uri에서 Bitmap을 가져오기
    val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri))

    val compressedFile = File(context.cacheDir, "compressed_image.jpg")
    val outputStream = FileOutputStream(compressedFile)

    val resizedBitmap = resizeBitmap(bitmap, maxWidth = 800, maxHeight = 800) // 예시로 800x800으로 리사이즈
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

    //bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream) // quality 값을 낮추면 용량이 줄어듭니다
    outputStream.flush()
    outputStream.close()

    // 압축된 파일을 RequestBody로 변환
    val requestFile: RequestBody = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

    // MultipartBody.Part 생성
    return MultipartBody.Part.createFormData(partName, compressedFile.name, requestFile)
}

fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
    val width = if (aspectRatio > 1) maxWidth else (maxHeight * aspectRatio).toInt()
    val height = if (aspectRatio > 1) (maxWidth / aspectRatio).toInt() else maxHeight
    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

fun getPathFromUri(context: Context, uri: Uri): String {
    var path: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA) // 가져올 데이터 컬럼 지정
    val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null) // ContentResolver로 데이터 쿼리
    cursor?.use {
        if (it.moveToFirst()) { // 첫 번째 데이터로 이동
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            path = it.getString(columnIndex) // 경로 추출
        }
    }
    return path ?: throw IllegalArgumentException("Unable to retrieve path from Uri") // 경로 반환
}