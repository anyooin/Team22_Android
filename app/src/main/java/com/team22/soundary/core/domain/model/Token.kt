package com.team22.soundary.core.domain.model

data class Token (
    val accessToken : String = "init",
    val refreshToken : String = "init"
)