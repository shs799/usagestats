package com.privacyFirst.usageStats.staticlib.string

import kotlin.math.min
import android.util.Pair


object StripString {
    fun packageNameStrip(p: Pair<String, String>): Pair<String, String> {
        val s1 = p.first
        val s2 = p.second
        val min = min(s1.length, s2.length)
        for (i in 0 until min)
            if (s1[i] != s2[i])
                return Pair(s1.substring(i), s2.substring(i))
        return Pair(s1.substring(min), s2.substring(min))
    }

    fun stripDot(s: String): String {
        for (i in s.indices)
            if (s[i] != '.')
                return s.substring(i)
        return s
    }
}