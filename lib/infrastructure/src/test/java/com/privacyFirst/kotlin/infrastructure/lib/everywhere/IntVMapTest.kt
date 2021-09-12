package com.privacyFirst.kotlin.infrastructure.lib.everywhere

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.privacyFirst.kotlin.infrastructure.lib.android.IntVMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class IntVMapTest {

    @Test fun sizeProperty() {
        val array = IntVMap<String>()
        assertEquals(0, array.size)
        array.put(1, "one")
        assertEquals(1, array.size)
    }

    @Test fun containsOperator() {
        val array = IntVMap<String>()
        assertFalse(1 in array)
        array.put(1, "one")
        assertTrue(1 in array)
    }

    @Test fun containsOperatorWithItem() {
        val array = IntVMap<String>()

        array.put(1, "one")
        assertFalse(2 in array)

        array.put(2, "two")
        assertTrue(2 in array)
    }

    @Test fun setOperator() {
        val array = IntVMap<String>()
        array[1] = "one"
        assertEquals("one", array.get(1))
    }

    @Test fun plusOperator() {
        val first = IntVMap<String>().apply { put(1, "one") }
        val second = IntVMap<String>().apply { put(2, "two") }
        val combined = first + second
        assertEquals(2, combined.size)
        //assertEquals(1, combined.keyAt(0))
        //assertEquals("one", combined.valueAt(0))
        //assertEquals(2, combined.keyAt(1))
        //assertEquals("two", combined.valueAt(1))
    }

    @Test fun getOrDefault() {
        val array = IntVMap<Any>()
        val default = Any()
        assertSame(default, array.getOrDefault(1, default))
        array.put(1, "one")
        assertEquals("one", array.getOrDefault(1, default))
    }

    @Test fun getOrElse() {
        val array = IntVMap<Any>()
        val default = Any()
        assertSame(default, array.getOrElse(1) { default })
        array.put(1, "one")
        //assertEquals("one", array.getOrElse(1) { fail() })
    }

    @Test fun isNotEmpty() {
        val array = IntVMap<String>()
        assertFalse(array.isNotEmpty())
        array.put(1, "one")
        assertTrue(array.isNotEmpty())
    }

    @Test fun pairIterator() {
        val array = IntVMap<String>()
        assertFalse(array.entries.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.entries.iterator()
        assertTrue(iterator.hasNext())
        val n1=iterator.next()
        assertEquals(1, n1.key)
        assertEquals("one", n1.value)

        n1.setValue("99")
        assertEquals("99",array.get(1))
        assertEquals("1=99",n1.toString())
        n1.setValue("one")

        assertTrue(iterator.hasNext())
        val n2=iterator.next()
        assertEquals(2,n2.key )
        assertEquals("two",n2.value )
        iterator.remove()
        assertTrue(iterator.hasNext())
        val n3=iterator.next()
        assertEquals(6, n3.key)
        assertEquals("six",n3.value )
        assertFalse(iterator.hasNext())

        val iterator2 = array.entries.iterator()
        assertTrue(iterator2.hasNext())
        val o1=iterator2.next()
        assertEquals(1, o1.key)
        assertEquals("one", o1.value)
        assertTrue(iterator2.hasNext())
        val o3=iterator2.next()
        assertEquals(6, o3.key)
        assertEquals("six",o3.value )
        assertFalse(iterator2.hasNext())
    }

    @Test fun pairIterator2() {
        val array = IntVMap<String>()
        assertFalse(array.entries.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.entries.iterator()
        assertTrue(iterator.hasNext())
        val n1=iterator.next()
        assertEquals(1, n1.key)
        assertEquals("one", n1.value)
        iterator.remove()
        assertTrue(iterator.hasNext())
        val n2=iterator.next()
        assertEquals(2,n2.key )
        assertEquals("two",n2.value )
        assertTrue(iterator.hasNext())
        val n3=iterator.next()
        assertEquals(6, n3.key)
        assertEquals("six",n3.value )
        assertFalse(iterator.hasNext())

        val iterator2 = array.entries.iterator()
        assertTrue(iterator2.hasNext())
        val o1=iterator2.next()
        assertEquals(2, o1.key)
        assertEquals("two", o1.value)
        assertTrue(iterator2.hasNext())
        val o3=iterator2.next()
        assertEquals(6, o3.key)
        assertEquals("six",o3.value )
        assertFalse(iterator2.hasNext())
    }
    @Test fun pairIterator3() {
        val array = IntVMap<String>()
        assertFalse(array.entries.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.entries.iterator()
        assertTrue(iterator.hasNext())
        val n1=iterator.next()
        assertEquals(1, n1.key)
        assertEquals("one", n1.value)

        assertTrue(iterator.hasNext())
        val n2=iterator.next()
        assertEquals(2,n2.key )
        assertEquals("two",n2.value )
        assertTrue(iterator.hasNext())
        val n3=iterator.next()
        assertEquals(6, n3.key)
        assertEquals("six",n3.value )
        iterator.remove()
        assertFalse(iterator.hasNext())

        val iterator2 = array.entries.iterator()
        assertTrue(iterator2.hasNext())
        val o1=iterator2.next()
        assertEquals(1, o1.key)
        assertEquals("one", o1.value)
        assertTrue(iterator2.hasNext())
        val o2=iterator2.next()
        assertEquals(2,o2.key )
        assertEquals("two",o2.value )
        assertFalse(iterator2.hasNext())
    }
    @Test fun keyIterator() {
        val array = IntVMap<String>()
        assertFalse(array.keys.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.keys.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(2, iterator.next())
        iterator.remove()
        assertTrue(iterator.hasNext())
        assertEquals(6, iterator.next())
        assertFalse(iterator.hasNext())

        val iterator2 = array.keys.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals(1, iterator2.next())
        assertTrue(iterator2.hasNext())
        assertEquals(6, iterator2.next())
        assertFalse(iterator2.hasNext())
    }

    @Test fun keyIterator2() {
        val array = IntVMap<String>()
        assertFalse(array.keys.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.keys.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())
        iterator.remove()
        assertTrue(iterator.hasNext())
        assertEquals(2, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(6, iterator.next())
        assertFalse(iterator.hasNext())

        val iterator2 = array.keys.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals(2, iterator2.next())
        assertTrue(iterator2.hasNext())
        assertEquals(6, iterator2.next())
        assertFalse(iterator2.hasNext())
    }
    @Test fun keyIterator3() {
        val array = IntVMap<String>()
        assertFalse(array.keys.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.keys.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(2, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(6, iterator.next())
        iterator.remove()
        assertFalse(iterator.hasNext())

        val iterator2 = array.keys.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals(1, iterator2.next())
        assertTrue(iterator2.hasNext())
        assertEquals(2, iterator2.next())
        assertFalse(iterator2.hasNext())
    }

    @Test fun valueIterator() {
        val array = IntVMap<String>()
        assertFalse(array.values.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.values.iterator()
        assertTrue(iterator.hasNext())
        assertEquals("one", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("two", iterator.next())
        iterator.remove()
        assertTrue(iterator.hasNext())
        assertEquals("six", iterator.next())
        assertFalse(iterator.hasNext())

        val iterator2 = array.values.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals("one", iterator2.next())
        assertTrue(iterator2.hasNext())
        assertEquals("six", iterator2.next())
        assertFalse(iterator2.hasNext())
    }

    @Test fun valueIterator2() {
        val array = IntVMap<String>()
        assertFalse(array.values.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.values.iterator()
        assertTrue(iterator.hasNext())
        assertEquals("one", iterator.next())
        iterator.remove()
        assertTrue(iterator.hasNext())
        assertEquals("two", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("six", iterator.next())
        assertFalse(iterator.hasNext())

        val iterator2 = array.values.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals("two", iterator2.next())
        assertTrue(iterator2.hasNext())
        assertEquals("six", iterator2.next())
        assertFalse(iterator2.hasNext())
    }
    @Test fun valueIterator3() {
        val array = IntVMap<String>()
        assertFalse(array.values.iterator().hasNext())

        array.put(1, "one")
        array.put(2, "two")
        array.put(6, "six")

        val iterator = array.values.iterator()
        assertTrue(iterator.hasNext())
        assertEquals("one", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("two", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("six", iterator.next())
        iterator.remove()
        assertFalse(iterator.hasNext())

        val iterator2 = array.values.iterator()
        assertTrue(iterator2.hasNext())
        assertEquals("one", iterator2.next())
        assertTrue(iterator2.hasNext())
        assertEquals("two", iterator2.next())
        assertFalse(iterator2.hasNext())
    }
}