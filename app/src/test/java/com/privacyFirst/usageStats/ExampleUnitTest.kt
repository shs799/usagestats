package com.privacyFirst.usageStats


import androidx.collection.ArrayMap
import com.privacyFirst.usageStats.staticlib.map.IntVMapC

import org.junit.Assert.*
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun put_get_remove() {
        val s= IntVMapC<Int>()
        //val a= ArrayMap<Int,Int>()

        assertEquals(null, s[1])
        assertEquals(null,s.put(1,2))
        assertEquals(2,s.put(1,1))
        assertEquals(null, s.put(2,2))

        assertEquals(2,s.remove(2))
        assertEquals(null,s.remove(2))
    }

    @Test
    fun putAll(){
        val s2= IntVMapC<Int>()
        val s= ArrayMap<Int,Int>()

        val e1= mapOf(90 to 1,2 to 2,4 to 3)
        s.putAll(e1)
        s2.putAll(e1)
        assertEquals(s,s2)

        val e2= mapOf(1 to 1,2 to 3,4 to 3)
        s.putAll(e2)
        s2.putAll(e2)
        assertEquals(s,s2)
    }
    @Test
    fun entries_add_remove() {
        val a= IntVMapC<Int>()
        val s= IntVMapC<Int>()

        //assertEquals(a,s)
        assertEquals(a[1], s[1])

        assertEquals(true,a.entries.add(IntVMapC.Node(1,2)))
        s.put(1,2)
        //assertEquals(a,s)
        assertEquals(a[1], s[1])
        assertEquals(a[2], s[2])

        assertEquals(true,a.entries.add(IntVMapC.Node(1,3)))
        s.put(1,3)
        //assertEquals(a,s)
        assertEquals(a[1], s[1])
        assertEquals(a[2], s[2])

        a.entries.add(IntVMapC.Node(2,3))
        s.put(2,3)
        //assertEquals(a,s)
        assertEquals(a[1], s[1])
        assertEquals(a[2], s[2])

        a.entries.remove(IntVMapC.Node(2, a.get(2)!! +1))
        //s.remove(2)
        //assertEquals(a,s)
        assertEquals(a[1], s[1])
        assertEquals(a[2], s[2])

        a.entries.remove(IntVMapC.Node(2, a.get(2)!!))
        s.remove(2)
        assertEquals(a.toString(),s.toString())
        assertEquals(a[1], s[1])
        assertEquals(a[2], s[2])

        a.entries.remove(IntVMapC.Node(9, 99))
        assertEquals(a.toString(),s.toString())
        assertEquals(a[1], s[1])
        assertEquals(a[2], s[2])
    }

    @Test
    fun entries_addAll(){
        val a= IntVMapC<Int>()
        val s= IntVMapC<Int>()
        assertEquals(false,s.entries.addAll(s.entries))
        val e1= mutableMapOf(90 to 1,2 to 2,4 to 3)
        s.entries.addAll(e1.entries)
        a.putAll(e1)
        assertEquals(s.toString(),a.toString())

        val e2= mutableMapOf(1 to 1,2 to 3,4 to 3)
        s.entries.addAll(e2.entries)
        a.putAll(e2)
        assertEquals(s.toString(),a.toString())
        assertEquals(false,s.entries.addAll(s.entries))

    }

}
