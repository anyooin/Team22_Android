package com.team22.soundary.core.domain.model

import java.util.Date

data class Share(
    val id: String = "",
    val song: Song = Song(),
    val message: String = "",
    val friend: User = User(),
    val isLike: Boolean = false,
    val sharedDate: Date = Date()
)
