package com.privacyFirst.kotlin.infrastructure.lib

import com.privacyFirst.kotlin.infrastructure.lib.android.collection.IntVMap
import org.junit.Test

class test {
    @Test
    fun a(){
        val a= IntVMap<Int>()
        a[1] = 1
        val hm=HashMap<Int,Int>()
        hm.entries
        for (i in 0 until a.size){
            println(i)
        }

    }
}