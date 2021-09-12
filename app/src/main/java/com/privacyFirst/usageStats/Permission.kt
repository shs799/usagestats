package com.privacyFirst.usageStats

import android.app.AppOpsManager
import android.content.Context
import android.os.Build
import android.os.Process
import androidx.core.content.getSystemService


object Permission {
    fun checkUsageStatsPermission(context: Context): Boolean {
        val appOps = context
            .getSystemService<AppOpsManager>()
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps?.unsafeCheckOpNoThrow("android:get_usage_stats",
                Process.myUid(), context.packageName)
        } else {
            appOps?.checkOpNoThrow("android:get_usage_stats",
                Process.myUid(), context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
}