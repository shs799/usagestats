package com.privacyFirst.usageStats

import java.text.SimpleDateFormat
import java.util.*

object DateTrans {
    fun transDateFilter(first: Long, target: Long): String {
        if(target<24*60*60*1000){
            return transDate(target+first)
        }
        /*if ((first%(24*60*60*1000))==target){
            return 0
        }*/
        return transDate(target)
    }
    fun transDate(currentTimeMillis: Long): String {
        return if (currentTimeMillis == 0L) ""
        else SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z").format(Date(currentTimeMillis))
    }

    fun transTime(timeMillis: Long): String {
        if (timeMillis == 0L) return "never"
        val s = SimpleDateFormat("-HH:mm:ss::SSS")
        s.timeZone = TimeZone.getTimeZone("UTC")
        return (timeMillis / (1000 * 60 * 60 * 24)).toString() + s.format(timeMillis)
    }
}
internal abstract class Person {
    abstract fun eat()
}

