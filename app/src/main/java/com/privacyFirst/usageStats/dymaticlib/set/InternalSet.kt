package com.privacyFirst.usageStats.dymaticlib.set

import android.os.Build
import android.util.ArraySet
import androidx.annotation.RequiresApi

object InternalSet {
    @RequiresApi(Build.VERSION_CODES.M)
    fun <T> arraySetOf(vararg values: T): ArraySet<T> {
        val set = ArraySet<T>(values.size)
        for (value in values)
            set.add(value)
        return set
    }
}