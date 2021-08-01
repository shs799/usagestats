package com.privacyFirst.usageStats

import java.text.SimpleDateFormat
import java.util.*

class DateTrans {
    fun transDateFilter(currentTimeMillis: Long): String {
        if (currentTimeMillis < 24 * 60 * 60 * 1000) return ""
        return transDate(currentTimeMillis)
    }

    fun transDate(currentTimeMillis: Long): String {
        if (currentTimeMillis == 0L) return ""
        return SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z").format(Date(currentTimeMillis))
    }

    fun transTime(timeMillis: Long): String {
        if(timeMillis==0L)return "never"
        val s = SimpleDateFormat("-HH:mm:ss::SSS")
        s.timeZone = TimeZone.getTimeZone("UTC")
        return (timeMillis / (1000 * 60 * 60 * 24)).toString() + s.format(timeMillis)
        //val SimpleDateFormat("D-HH:mm:ss:SSS")
    }
}