package com.team22.soundary.extensions

import android.util.Log
import java.util.Calendar
import java.util.Date


fun Date.getDiff() : String {
    val cur = Calendar.getInstance().time
    val diff = cur.time - this.time

    val second = (diff / 1000).toInt()
    val minute = second / 60
    val hour = minute / 60
    val day = hour / 24
    val month = day / 30

    return if(month > 0){
        "${month}월"
    } else if(day > 0){
       "${day}일"
    } else if(hour > 0){
        "${hour}시간"
    } else if(minute > 0){
        "${minute}분"
    } else{
        "${second}초"
    }
}