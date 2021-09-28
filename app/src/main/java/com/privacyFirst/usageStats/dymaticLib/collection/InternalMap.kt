package com.privacyFirst.usageStats.dymaticLib.collection

import android.os.Build
import android.util.Pair
import androidx.annotation.RequiresApi

object InternalMap {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun <K, V> arrayMapOf(vararg pairs: Pair<K, V>): android.util.ArrayMap<K, V> {
        val map = android.util.ArrayMap<K, V>(pairs.size)
        for (pair in pairs) map[pair.first] = pair.second
        return map
    }

    fun <K, V> autoArrayMapOf(vararg pairs: Pair<K, V>): MutableMap<K, V> {
        val map = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            android.util.ArrayMap<K, V>(pairs.size)
        else androidx.collection.ArrayMap<K, V>(pairs.size)
        for (pair in pairs) map[pair.first] = pair.second
        return map
    }
}