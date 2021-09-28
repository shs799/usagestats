package com.privacyFirst.usageStats.staticLib.string

import kotlin.math.min

object CompareString {
    fun checkInitial(s1:String,s2: String): Int {
        val min = min(s1.length, s2.length)
        var i=0
        while(i<min){
            if (s1[i] != s2[i])
                return i.inv()
            i++
        }
        return i
    }
}