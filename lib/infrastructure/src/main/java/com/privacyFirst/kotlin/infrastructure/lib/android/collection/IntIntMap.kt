package com.privacyFirst.kotlin.infrastructure.lib.android.collection

/*
IntIntMap
IntIntMap is a Integer-to-Object-Map based on SparseIntArray. It implemented the MutableMap interface, So you can downgrade to SparseIntArray faster.
It is based on AndroidX package, so it can run everywhere.
Iot of bug exist. Reporting are welcome.

//downgrade tutorial from ArrayMap to IntIntMap
//from:
val map:MutableMap<Int,Int>=ArrayMap<Int,Int>()
//to:
val map:MutableMap<Int,Int>=IntIntMap<Int>()

//bind exist SparseIntArray to MutableMap
val sparseIntArray=SparseIntArray()
val map:MutableMap<Int,Int>=IntIntMap<Int>(sparseIntArray)
 */

//run everywhere

//warning: thread not-safe

import android.os.Build
import android.util.SparseIntArray
import androidx.annotation.RequiresApi
import androidx.core.util.containsKey
import androidx.core.util.containsValue
import androidx.core.util.isEmpty
import androidx.core.util.remove
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.EntrySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.KeySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.NodeA
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.ValueListI
import java.util.*

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class IntIntMap : MutableMap<Int, Int> {
    var sparseIntArray: SparseIntArray

    constructor() {
        sparseIntArray = SparseIntArray()
    }

    constructor(size: Int) {
        sparseIntArray = SparseIntArray(size)
    }

    constructor(sparseIntArray: SparseIntArray) {
        this.sparseIntArray = sparseIntArray
    }

    override val size get() = sparseIntArray.size()
    override fun containsKey(key: Int) = sparseIntArray.containsKey(key)

    override fun containsValue(value: Int) = sparseIntArray.containsValue(value)

    override fun get(key: Int): Int? {
        val ret = sparseIntArray.get(key)
        if (ret == 0) {
            if (!containsKey(key))
                return null
        }
        return ret
    }

    override fun getOrDefault(key: Int, defaultValue: Int): Int =
        sparseIntArray.get(key, defaultValue)

    override fun isEmpty() = sparseIntArray.isEmpty()
    override val entries: MutableSet<MutableMap.MutableEntry<Int, Int>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val nes = EntrySet(sparseIntArray)
                mEntrySet = nes
                nes
            } else es
        }
    private var mEntrySet: MutableSet<MutableMap.MutableEntry<Int, Int>>? = null

    override val keys: MutableSet<Int>
        get() {
            val k = mKeys
            return if (k == null) {
                val nk = KeySet(sparseIntArray)
                mKeys = nk
                nk
            } else k
        }
    private var mKeys: KeySet? = null

    override val values: MutableCollection<Int>
        get() {
            val v = mValues
            return if (v == null) {
                val nv = ValueList(sparseIntArray)
                mValues = nv
                nv
            } else v
        }
    private var mValues: MutableCollection<Int>? = null
    override fun clear() = sparseIntArray.clear()

    override fun put(key: Int, value: Int): Int? {
        val old = get(key)
        sparseIntArray.put(key, value)
        return old
    }

    override fun putAll(from: Map<out Int, Int>) =
        from.entries.forEach { i -> sparseIntArray.append(i.key, i.value) }

    override fun remove(key: Int): Int? {
        val oldValue = get(key)
        if (oldValue != null) sparseIntArray.remove(key, oldValue)
        return oldValue
    }

    override fun toString() =
        sparseIntArray.toString()


    fun newNode(key: Int, value: Int): MutableMap.MutableEntry<Int, Int> {
        return Node(key, value)
    }

    private class Node<Int>(override val key: Int, override var value: Int) : NodeA<Int, Int>() {
        override fun setValue(newValue: Int): Int {
            val old = value
            value = newValue
            return old
        }

        override fun toString() =
            "$key=$value"
    }

    class ValueIterator(private val sparseIntArray: SparseIntArray) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryIntalid = false
        override fun hasNext() = point + (if (mEntryIntalid) 1 else 0) < sparseIntArray.size()

        override fun remove() {
            check(mEntryIntalid)
            sparseIntArray.removeAt(point)
            mEntryIntalid = false
        }

        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryIntalid) point++
            mEntryIntalid = true
            return sparseIntArray.valueAt(point)
        }
    }

    class ValueList(private val sparseIntArray: SparseIntArray) :
        ValueListI<Int> {
        override val size: Int
            get() = sparseIntArray.size()

        override fun contains(element: Int) = sparseIntArray.containsValue(element)

        override fun isEmpty() = sparseIntArray.isEmpty()

        override fun iterator() = ValueIterator(sparseIntArray)

        override fun clear() = sparseIntArray.clear()

        override fun toString() = sparseIntArray.toString()

    }

    class KeyIterator(private val sparseIntArray: SparseIntArray) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryIntalid = false
        private var key: Int = 0
        override fun hasNext() =
            point + (if (mEntryIntalid) 1 else 0) < sparseIntArray.size()


        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryIntalid) point++
            mEntryIntalid = true
            val k = sparseIntArray.keyAt(point)
            key = k
            return k
        }

        override fun remove() {
            check(mEntryIntalid)
            val index = sparseIntArray.indexOfKey(key)
            check(index >= 0)
            if (index >= 0) sparseIntArray.removeAt(index)
            mEntryIntalid = false
        }
    }

    class KeySet(
        private val sparseIntArray: SparseIntArray,
    ) : KeySetI<Int> {

        override fun clear() = sparseIntArray.clear()

        override fun iterator() = KeyIterator(sparseIntArray)

        override fun remove(element: Int): Boolean {
            val index = sparseIntArray.indexOfKey(element)
            return if (index > 0) {
                sparseIntArray.removeAt(index)
                true
            } else false
        }

        override val size: Int get() = sparseIntArray.size()

        override fun contains(element: Int) = sparseIntArray.containsKey(element)

        override fun isEmpty() = sparseIntArray.isEmpty()

        override fun toString() = sparseIntArray.toString()

    }

    class EntryIterator(private val sparseIntArray: SparseIntArray) :
        MutableIterator<MutableMap.MutableEntry<Int, Int>> {
        private var point: Int = 0
        private var mEntryValid = false
        private var key: Int = 0
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < sparseIntArray.size()

        override fun next(): MutableMap.MutableEntry<Int, Int> {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            val k = sparseIntArray.keyAt(point)
            key = k
            return InternalNode(k, sparseIntArray)
        }

        override fun remove() {
            check(mEntryValid)
            val index = sparseIntArray.indexOfKey(key)
            check(index >= 0)
            if (index >= 0) sparseIntArray.removeAt(index)
            mEntryValid = false
        }

        private class InternalNode(
            private val k: Int,
            private val sparseIntArray: SparseIntArray,
        ) : NodeA<Int, Int>() {
            override val key: Int
                get() = k
            override val value: Int
                get() = sparseIntArray.get(k)

            override fun setValue(newValue: Int): Int {
                val old = value
                sparseIntArray.put(k, newValue)
                return old
            }
        }
    }


    class EntrySet(private val sparseIntArray: SparseIntArray) :
        EntrySetI<Int, Int> {
        override fun add(element: MutableMap.MutableEntry<Int, Int>): Boolean {
            val index = sparseIntArray.indexOfKey(element.key)
            if (index >= 0) if (sparseIntArray.valueAt(index) == element.value) return false
            sparseIntArray.put(element.key, element.value)
            return true

        }

        override fun clear() = sparseIntArray.clear()

        override fun iterator() = EntryIterator(sparseIntArray)

        override fun remove(element: MutableMap.MutableEntry<Int, Int>): Boolean {
            val k = element.key
            val index = sparseIntArray.indexOfKey(k)
            if (index >= 0) {
                if (sparseIntArray.valueAt(index) == element.value) {
                    sparseIntArray.removeAt(index)
                    return true
                }
            }
            return false
        }


        override val size: Int = sparseIntArray.size()

        override fun contains(element: MutableMap.MutableEntry<Int, Int>): Boolean {
            val index = sparseIntArray.indexOfKey(element.key)
            return if (index >= 0) sparseIntArray.valueAt(index) == element.value else false
        }

        override fun isEmpty() = sparseIntArray.isEmpty()

        override fun toString() = sparseIntArray.toString()
    }
}