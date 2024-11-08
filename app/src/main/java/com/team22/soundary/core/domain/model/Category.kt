package com.team22.soundary.core.domain.model

import android.util.Log
import com.team22.soundary.R

enum class Category {
    RNB, HIPHOP, POP, ROCK, JPOP, KPOP
}

fun stringListToEnumList(stringList: List<String>): List<Category> {
    return stringList.mapNotNull { string ->
        try {
            Category.valueOf(string.uppercase()) // 문자열을 Enum으로 변환, 대소문자 무시
        } catch (e: IllegalArgumentException) {
            null // 매칭되지 않는 문자열은 무시
        }
    }
}

fun getCategoryMap(): Map<Category,Int> = mapOf(
    Pair(Category.HIPHOP,0),
    Pair(Category.ROCK,1),
    Pair(Category.POP,2),
    Pair(Category.JPOP,3),
    Pair(Category.RNB,4),
    Pair(Category.KPOP,5))

fun stringToCategory(categoryString : String) : Category? {
    val category = when (categoryString) {
        "RNB" -> Category.RNB
        "HIPHOP" -> Category.HIPHOP
        "POP" -> Category.POP
        "ROCK" -> Category.ROCK
        "JPOP" -> Category.JPOP
        "KPOP" -> Category.KPOP
        else -> null
    }
    return category
}

