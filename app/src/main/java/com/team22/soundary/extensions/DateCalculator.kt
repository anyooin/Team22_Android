package com.team22.soundary.extensions

import java.util.Calendar
import java.util.Date
import kotlin.math.abs


fun Date.getDiff(): String {

    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.HOUR, 9)  // 9시간 추가

    val cur = Calendar.getInstance().time
    val diff = abs(cur.time - calendar.time.time)
    val suffix = if (cur.time >= calendar.time.time) "전" else "후"

    val second = (diff / 1000).toInt()
    val minute = second / 60
    val hour = minute / 60
    val day = hour / 24
    val month = day / 30


    return if (month > 0) {
        "${month}개월"
    } else if (day > 0) {
        "${day}일"
    } else if (hour > 0) {
        "${hour}시간"
    } else if (minute > 0) {
        "${minute}분"
    } else {
        "${second}초"
    } + suffix

}

