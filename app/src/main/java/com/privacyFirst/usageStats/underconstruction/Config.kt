package com.privacyFirst.usageStats.underconstruction

import com.privacyFirst.usageStats.dymaticLib.slimOrFastestMap.MapDowngradeIntV
import com.privacyFirst.usageStats.staticLib.string.CompareString.checkInitial
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.staticProperties

object config {
    inline fun <reified T> a(title: String): Map<Int, String> {
        val c = T::class
        val chm = ConcurrentHashMap<Int, String>()
        c.staticProperties.parallelStream().filter {
            checkInitial(it.name, title.uppercase(Locale.getDefault()) + "_") >= 0
        }.forEachOrdered {
            val i = checkInitial(it.name, title.uppercase(Locale.getDefault()) + "_")
            val k = it.get()
            if (k is Int) chm.put(k, it.name.substring(i))
        }
        return MapDowngradeIntV(chm)
    }
}