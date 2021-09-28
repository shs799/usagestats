package com.privacyFirst.usageStats

import android.app.usage.UsageStats
import android.os.Build
import com.privacyFirst.usageStats.dymaticLib.collection.InternalSet.autoArraySetOf
import com.privacyFirst.usageStats.staticLib.date.DateTrans
import java.util.*
import java.util.concurrent.ConcurrentSkipListMap

object UsageStatsClass {

    private val blockListFieldsName: Set<String>
        get() =
            autoArraySetOf(
                "mPackageName",

                "mBeginTimeStamp",
                "mEndTimeStamp",
                "mLastTimeUsed",
                "mTotalTimeInForeground",

                "mTotalTimeForegroundServiceUsed",
                "mLastTimeVisible",
                "mLastTimeForegroundServiceUsed",
                "mTotalTimeVisible",

                "mPackageToken",

                "CREATOR")


    private val nullValue: Set<String>
        get() =
            autoArraySetOf("{}", "0", "-1", "null")


    private val getSBInitLength: Int
        get() {
            val stampExample = "yyyy-MM-dd HH:mm:ss::SSS z0000"
            val timeExample = "0-HH:mm:ss::SSS"

            val s = StringBuilder()
            s.append("packageName: " + "\n")
            s.append("\n")
            s.append("firstTimeStamp: $stampExample\n")
            s.append("lastTimeStamp: $stampExample\n")
            s.append("lastTimeUsed: $stampExample\n")
            s.append("totalTimeInForeground: $timeExample\n")
            s.append("\n")
            s.append("firstInstallTime: $stampExample\n")
            s.append("lastUpdateTime: $stampExample\n")
            s.append("\n")
            return s.length
        }

    fun usageStatsToString(ua: UsageStats, firstInstallTime: Long, lastUpdateTime: Long): String {

        val sb = StringBuilder(getSBInitLength)
        sb.append("packageName: " + ua.packageName + "\n")
        sb.append("\n")
        sb.append("firstTimeStamp: " + DateTrans.stamp(ua.firstTimeStamp) + "\n")
        sb.append("lastTimeStamp: " + DateTrans.stamp(ua.lastTimeStamp) + "\n")
        sb.append("\n")
        sb.append("lastTimeUsed: " + DateTrans.dateFilter(ua.lastTimeUsed) + "\n")
        sb.append("totalTimeInForeground: " + DateTrans.time(ua.totalTimeInForeground) + "\n")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sb.append("totalTimeForegroundServiceUsed: " + DateTrans.time(ua.totalTimeForegroundServiceUsed) + "\n")
            sb.append("lastTimeVisible: " + (DateTrans.dateFilter(ua.lastTimeVisible)) + "\n")
            sb.append("lastTimeForegroundServiceUsed:" + DateTrans.dateFilter(ua.lastTimeForegroundServiceUsed) + "\n")
            sb.append("totalTimeVisible: " + DateTrans.time(ua.totalTimeVisible) + "\n")
        }
        sb.append("\n")
        if (firstInstallTime != 0L)
            sb.append("firstInstallTime: " + DateTrans.stamp(firstInstallTime) + "\n")
        if (lastUpdateTime != 0L) sb.append("lastUpdateTime: " + DateTrans.stamp(lastUpdateTime) + "\n")
        sb.append("\n")

        val fieldArray = ua.javaClass.declaredFields
        val cm = ConcurrentSkipListMap<String, String>()//key value
        Arrays.stream(fieldArray).parallel().filter { i ->
            !blockListFieldsName.contains(i.name)
        }
            .forEach { i ->
                i.isAccessible = true
                val get = i.get(ua)
                val typeName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                    i.genericType.typeName else i.genericType.toString()
                val v = if (i.name.contains("TimeUsed") && typeName == "long") {
                    if (get != null) {
                        val long = get as Long
                        if (long != 0L) DateTrans.stamp(long) else null
                    } else null
                } else if (i.name.contains("Time") && typeName == "long") {
                    if (get != null) {
                        val long = get as Long
                        if (long != 0L) DateTrans.time(long) else null
                    } else null
                } else {
                    get?.toString()
                }

                if (!nullValue.contains(v) && v != null)
                    cm[i.name] = v

                i.isAccessible = false
            }

        cm.entries.stream().forEach { (k, v) ->
            sb.append("#$k: $v\n")
        }
        return sb.toString()
    }

}