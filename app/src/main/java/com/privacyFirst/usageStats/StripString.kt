package com.privacyFirst.usageStats

import kotlin.Pair.first
import kotlin.Pair.second

object stripString {
    fun strip(p: Pair<String, String>): Pair<String, String> {
        val s1 = p.first
        val s2 = p.second
        val min = Math.min(s1.length, s2.length)
        for (i in 0 until min) {
            if (s1[i] != s2[i]) {
                return Pair(s1.substring(i), s2.substring(i))
            }
        }
        return Pair(s1.substring(min), s2.substring(min))
    }

    fun stripDot(s: String): String {
        for (i in 0 until s.length) {
            if (s[i] != '.') {
                return s.substring(i)
            }
        }
        return s
    }
}