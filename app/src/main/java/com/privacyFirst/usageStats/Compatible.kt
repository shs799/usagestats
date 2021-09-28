package com.privacyFirst.usageStats

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.core.content.getSystemService


object Compatible {
    //api28-29
    fun checkUsageStatsPermission(context: Context): Boolean {
        val appOps = context
            .getSystemService<AppOpsManager>()
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps?.unsafeCheckOpNoThrow("android:get_usage_stats",
                Process.myUid(),
                context.packageName)
        } else {
            appOps?.checkOpNoThrow("android:get_usage_stats",
                Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    //api21-22
    fun getUsageStatsManager(context: Context): UsageStatsManager? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
            context.getSystemService()
        else {
            val c = Context.USAGE_STATS_SERVICE
            val ret = context.getSystemService(c)
            if (ret is UsageStatsManager) ret else null
        }
    }
}