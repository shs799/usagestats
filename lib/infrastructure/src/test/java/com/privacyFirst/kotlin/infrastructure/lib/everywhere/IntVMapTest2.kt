package com.privacyFirst.kotlin.infrastructure.lib.everywhere

import androidx.collection.ArrayMap
import com.privacyFirst.kotlin.infrastructure.lib.android.collection.IntVMap
import org.junit.Assert.*
import org.junit.Test

class IntVMapTest2 {
    fun <K,V> mutableMapTest(a:MutableMap<K,V>,b:MutableMap<K,V>)=
         a.entries.containsAll(b.entries) and b.entries.containsAll(a.entries)

    @Test
    fun put() {
        val i = IntVMap<Int>()
        val h=HashMap<Int,Int>()
        assertEquals(i.put(1, 3),h.put(1,3))
        assertTrue(mutableMapTest(i,h))
    }

    @Test
    fun remove() {
        val i = IntVMap<Int>()
        i[1] = 3
        assertEquals(3, i.remove(1))
        assertNull(i.remove(2))
    }
    @Test
    fun containsAll() {
        val hm1=ArrayMap<Int,String>()
        hm1[1] = "one"

        val i = IntVMap<String>()
        i[1] = "one"
        assertTrue(i.values.containsAll(i.values))
        assertTrue(i.keys.containsAll(i.keys))
        assertTrue(i.entries.containsAll(i.entries))

        assertTrue(i.values.containsAll(hm1.values))
        assertTrue(i.keys.containsAll(hm1.keys))
        assertTrue(i.entries.containsAll(hm1.entries))
        hm1[2] = "two"
        assertFalse(i.values.containsAll(hm1.values))
        assertFalse(i.keys.containsAll(hm1.keys))
        assertFalse(i.entries.containsAll(hm1.entries))
    }

    @Test
    fun contains() {
        val i = IntVMap<String>()
        i[1] = "one"
        val n1= i.newNode(1,"one")
        assertTrue(i.entries.contains(n1))
        val n2= i.newNode(2,"one")
        assertFalse(i.entries.contains(n2))
        val n3= i.newNode(1,"two")
        assertFalse(i.entries.contains(n3))
    }


}