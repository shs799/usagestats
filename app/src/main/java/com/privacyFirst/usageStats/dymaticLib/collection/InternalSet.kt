package com.privacyFirst.usageStats.dymaticLib.collection

import android.os.Build
import androidx.annotation.RequiresApi

object InternalSet {
    @RequiresApi(Build.VERSION_CODES.M)
    fun <T> arraySetOf(vararg values: T): android.util.ArraySet<T> {
        val set = android.util.ArraySet<T>(values.size)
        for (value in values)
            set.add(value)
        return set
    }

    fun <T> autoArraySetOf(vararg values: T): MutableSet<T> {
        val set = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.util.ArraySet<T>(values.size)
        } else {
            androidx.collection.ArraySet<T>(values.size)
        }
        for (value in values)
            set.add(value)
        return set
    }
}