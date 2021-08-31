package com.privacyFirst.usageStats.staticlib.staticm

object DateToday {
    fun todayInUtcMilliseconds():Long{
        val s=System.currentTimeMillis()
        return s-s%(24*60*60*1000)
    }
}