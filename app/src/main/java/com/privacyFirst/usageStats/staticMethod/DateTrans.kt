package com.privacyFirst.usageStats.staticMethod

import java.text.SimpleDateFormat
import java.util.*

object DateTrans {
    private val localDateStamp = ThreadLocal.withInitial {
        SimpleDateFormat("y-MM-dd Z", Locale.getDefault())
    }
    private val localTimeStamp = ThreadLocal.withInitial {
        SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z", Locale.getDefault())
    }
    private val localTime = ThreadLocal.withInitial {
        SimpleDateFormat("-HH:mm:ss::SSS", Locale.getDefault())
    }
    private val localUTCTimeZone = ThreadLocal.withInitial {
        TimeZone.getTimeZone("UTC")
    }

    fun dateFilter(target: Long): String {
        if (target / (24 * 60 * 60 * 1000) == 0L)
            return "earlier"
        return stamp(target)
    }

    fun dateStamp(currentTimeMillis: Long): String {
        return localDateStamp.get()!!.format(Date(currentTimeMillis))
    }

    fun stamp(currentTimeMillis: Long): String {
        return if (currentTimeMillis == 0L) ""
        else localTimeStamp.get()!!.format(
            Date(
                currentTimeMillis
            )
        )
    }

    fun time(timeMillis: Long): String {
        if (timeMillis == 0L) return "never"
        val s = localTime.get()!!
        s.timeZone = localUTCTimeZone.get()!!
        return (timeMillis / (1000 * 60 * 60 * 24)).toString() + s.format(timeMillis)
    }
}
