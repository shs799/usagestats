package com.privacyFirst.usageStats


import androidx.collection.ArrayMap
import com.privacyFirst.usageStats.method.sparse.IntVMapC
import org.junit.Test

import org.junit.Assert.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class tIntVMapC {
    @Test
    fun put_and_get() {
        val s= IntVMapC<Int>()
        val a= ArrayMap<Int,Int>()

        assertEquals(a,s)
        assertEquals(a.get(1),s.get(1))

        assertEquals(a.put(1,2),s.put(1,2))
        assertEquals(a,s)
        assertEquals(a.get(1),s.get(1))
        assertEquals(a.get(2),s.get(2))

        assertEquals(a.put(1,3),s.put(1,3))
        assertEquals(a,s)
        assertEquals(a.get(1),s.get(1))
        assertEquals(a.get(2),s.get(2))

        assertEquals(a.put(2,3),s.put(2,3))
        assertEquals(a,s)
        assertEquals(a.get(1),s.get(1))
        assertEquals(a.get(2),s.get(2))
    }

    @Test
    fun get(){



    }

    @Test
    fun putAll(){
        val s= IntVMapC<Int>()
        val a= IntVMapC<Int>()
        a.put(1,1)
        a.put(2,2)
        s.putAll(a)
        assertEquals(a,s)
    }
}