package com.team22.soundary.core.domain.model

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody


data class User(
    val id: String = "",
    val displayId: String = "",
    val name: String = "",
    val email: String = "",
    val imageId: String = "",
    val statusMessage: String = "",
    val label: List<String> = emptyList(),
    val role: List<String> = emptyList()
)

fun createImageMultipart(
    context: Context,
    imageUri: Uri?,
    partName: String = "image"
): MultipartBody.Part {
    val file = File(getPathFromUri(context, imageUri!!)) // Uri를 실제 파일 경로로 변환
    val requestFile: RequestBody =
        file.asRequestBody("image/*".toMediaTypeOrNull()) // 파일을 RequestBody로 변환
    return MultipartBody.Part.createFormData(
        partName,
        file.name,
        requestFile
    ) // MultipartBody.Part 생성
}

fun getPathFromUri(context: Context, uri: Uri): String {
    var path: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA) // 가져올 데이터 컬럼 지정
    val cursor: Cursor? =
        context.contentResolver.query(uri, projection, null, null, null) // ContentResolver로 데이터 쿼리
    cursor?.use {
        if (it.moveToFirst()) { // 첫 번째 데이터로 이동
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            path = it.getString(columnIndex) // 경로 추출
        }
    }
    return path ?: throw IllegalArgumentException("Unable to retrieve path from Uri") // 경로 반환
}