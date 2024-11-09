package com.team22.soundary.core.domain.model

import android.net.Uri

data class User(
    val id: String = "",
    val displayId: String = "",
    val name: String = "",
    val email: String = "",
    val image: Uri = Uri.EMPTY,
    val statusMessage: String = "",
    val category: List<Category> = emptyList(),
    val label : List<String> = emptyList(),
    var status: String = "",
    val role : List<String> = emptyList()
)
