package com.privacyFirst.usageStats.staticlib.date

import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

object DateTrans {
    private val localDateStamp: ThreadLocal<SimpleDateFormat>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ThreadLocal.withInitial {
                SimpleDateFormat("y-MM-dd Z", Locale.getDefault())
            }
         else
            null



    private val localTimeStamp: ThreadLocal<SimpleDateFormat>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ThreadLocal.withInitial {
                SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z", Locale.getDefault())
            }
         else
            null


    private val localTime: ThreadLocal<SimpleDateFormat>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ThreadLocal.withInitial {
                SimpleDateFormat("-HH:mm:ss::SSS", Locale.getDefault())
            }
         else
            null


    private val localUTCTimeZone: ThreadLocal<TimeZone>? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ThreadLocal.withInitial {
                TimeZone.getTimeZone("UTC")
            }
         else
            null


    fun dateFilter(target: Long): String {
        if (target / (24 * 60 * 60 * 1000) == 0L)
            return "earlier"
        return stamp(target)
    }

    fun dateStamp(currentTimeMillis: Long): String {
        val dateStamp = localDateStamp?.get() ?: SimpleDateFormat("y-MM-dd Z", Locale.getDefault())
        return dateStamp.format(Date(currentTimeMillis))
    }

    fun stamp(currentTimeMillis: Long): String {
        if (currentTimeMillis == 0L) return ""
        val timeStamp = localTimeStamp?.get() ?: SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z",
            Locale.getDefault())
        return timeStamp.format(Date(currentTimeMillis))
    }

    fun time(timeMillis: Long): String {
        if (timeMillis == 0L) return "never"
        val days = (timeMillis / (1000 * 60 * 60 * 24)).toString()
        val s = localTime?.get() ?: SimpleDateFormat("-HH:mm:ss::SSS", Locale.getDefault())
        s.timeZone = localUTCTimeZone?.get() ?: TimeZone.getTimeZone("UTC")
        return days + s.format(timeMillis)
    }
}
