package com.privacyFirst.usageStats.staticLib.date

import android.os.Build
import com.privacyFirst.usageStats.desugar.ThreadLocalWithInitial
import java.text.SimpleDateFormat
import java.util.*

object DateTrans {


    fun localDateStampFun(): SimpleDateFormat {
        return SimpleDateFormat("y-MM-dd Z", Locale.getDefault())
    }

    fun localTimeFun(): SimpleDateFormat {
        return SimpleDateFormat("-HH:mm:ss::SSS", Locale.getDefault())
    }

    fun localTimeStampFun(): SimpleDateFormat {
        return SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z", Locale.getDefault())
    }

    fun localUTCTimeZone(): TimeZone {
        return TimeZone.getTimeZone("UTC")
    }

    private val localDateStamp: ThreadLocal<SimpleDateFormat> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ThreadLocal.withInitial { localDateStampFun() }
        else ThreadLocalWithInitial { localDateStampFun() }

    private val localTimeStamp: ThreadLocal<SimpleDateFormat> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ThreadLocal.withInitial { localTimeStampFun() }
        else ThreadLocalWithInitial { localDateStampFun() }

    private val localTime: ThreadLocal<SimpleDateFormat> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ThreadLocal.withInitial { localTimeFun() }
        else ThreadLocalWithInitial { localTimeFun() }

    private val localUTCTimeZone: ThreadLocal<TimeZone> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ThreadLocal.withInitial { localUTCTimeZone() }
        else ThreadLocalWithInitial { localUTCTimeZone() }


    fun dateFilter(target: Long): String {
        return if (target / (24 * 60 * 60 * 1000) == 0L) "earlier" else stamp(target)
    }

    fun dateStamp(currentTimeMillis: Long): String {
        val dateStamp = localDateStamp.get() ?: localDateStampFun()
        return dateStamp.format(Date(currentTimeMillis))
    }

    fun stamp(currentTimeMillis: Long): String {
        if (currentTimeMillis == 0L) return ""
        val timeStamp = localTimeStamp.get() ?: localTimeStampFun()
        return timeStamp.format(Date(currentTimeMillis))
    }

    fun time(timeMillis: Long): String {
        if (timeMillis == 0L) return "never"
        val days = (timeMillis / (1000 * 60 * 60 * 24)).toString()
        val s = localTime.get() ?: localTimeFun()
        s.timeZone = localUTCTimeZone.get() ?: localUTCTimeZone()
        return days + s.format(timeMillis)
    }
}
