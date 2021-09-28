package com.privacyFirst.kotlin.infrastructure.lib.android.collection

/*
IntLongMap
IntLongMap is a Integer-to-Object-Map based on SparseLongArray. It implemented the MutableMap interface, So you can downgrade to SparseLongArray faster.
It is based on AndroidX package, so it can run everywhere.
Iot of bug exist. Reporting are welcome.

//downgrade tutorial from ArrayMap to IntLongMap
//from:
val map:MutableMap<Int,Long>=ArrayMap<Int,Long>()
//to:
val map:MutableMap<Int,Long>=IntLongMap<Long>()

//bind exist SparseLongArray to MutableMap
val sparseLongArray=SparseLongArray()
val map:MutableMap<Int,Long>=IntLongMap<Long>(sparseLongArray)
 */

//run everywhere

//warning: only get and getOrDefault method is thread safe

import android.os.Build
import android.util.SparseLongArray
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
class IntLongMap : MutableMap<Int, Long> {
    var sparseLongArray: SparseLongArray


    constructor() {
        sparseLongArray = SparseLongArray()
    }

    constructor(size: Int) {
        sparseLongArray = SparseLongArray(size)
    }

    constructor(sparseLongArray: SparseLongArray) {
        this.sparseLongArray = sparseLongArray
    }

    override val size get() = sparseLongArray.size()
    override fun containsKey(key: Int) = sparseLongArray.containsKey(key)

    override fun containsValue(value: Long) = sparseLongArray.containsValue(value)

    override fun get(key: Int): Long? {
        val ret = sparseLongArray.get(key)
        if (ret == 0L) {
            if (!containsKey(key))
                return null
        }
        return ret
    }

    override fun getOrDefault(key: Int, defaultValue: Long): Long =
        sparseLongArray.get(key, defaultValue)

    override fun isEmpty() = sparseLongArray.isEmpty()
    override val entries: MutableSet<MutableMap.MutableEntry<Int, Long>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val nes = EntrySet(sparseLongArray)
                mEntrySet = nes
                nes
            } else es
        }
    private var mEntrySet: MutableSet<MutableMap.MutableEntry<Int, Long>>? = null

    override val keys: MutableSet<Int>
        get() {
            val k = mKeys
            return if (k == null) {
                val nk = KeySet(sparseLongArray)
                mKeys = nk
                nk
            } else k
        }
    private var mKeys: KeySet? = null

    override val values: MutableCollection<Long>
        get() {
            val v = mValues
            return if (v == null) {
                val nv = ValueList(sparseLongArray)
                mValues = nv
                nv
            } else v
        }
    private var mValues: MutableCollection<Long>? = null
    override fun clear() = sparseLongArray.clear()

    override fun put(key: Int, value: Long): Long? {
        val old = get(key)
        sparseLongArray.put(key, value)
        return old
    }

    override fun putAll(from: Map<out Int, Long>) =
        from.entries.forEach { i -> sparseLongArray.append(i.key, i.value) }

    override fun remove(key: Int): Long? {
        val oldValue = get(key)
        if(oldValue!=null)sparseLongArray.remove(key,oldValue)
        return oldValue
    }

    override fun toString() =
        sparseLongArray.toString()


    fun newNode(key: Int, value: Long): MutableMap.MutableEntry<Int, Long> {
        return Node(key, value)
    }

    private class Node<Long>(override val key: Int, override var value: Long) : NodeA<Int, Long>() {
        override fun setValue(newValue: Long): Long {
            val old = value
            value = newValue
            return old
        }

        override fun toString() =
            "$key=$value"
    }

    class ValueIterator(private val sparseLongArray: SparseLongArray) :
        MutableIterator<Long> {
        private var point: Int = 0
        private var mEntryLongalid = false
        override fun hasNext() = point + (if (mEntryLongalid) 1 else 0) < sparseLongArray.size()

        override fun remove() {
            check(mEntryLongalid)
            sparseLongArray.removeAt(point)
            mEntryLongalid = false
        }

        override fun next(): Long {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryLongalid) point++
            mEntryLongalid = true
            return sparseLongArray.valueAt(point)
        }
    }

    class ValueList(private val sparseLongArray: SparseLongArray) :
        ValueListI<Long> {
        override val size: Int
            get() = sparseLongArray.size()

        override fun contains(element: Long) = sparseLongArray.containsValue(element)

        override fun isEmpty() = sparseLongArray.isEmpty()

        override fun iterator() = ValueIterator(sparseLongArray)

        override fun clear() = sparseLongArray.clear()

        override fun toString() = sparseLongArray.toString()

    }

    class KeyIterator(private val sparseLongArray: SparseLongArray) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryLongalid = false
        override fun hasNext() =
            point + (if (mEntryLongalid) 1 else 0) < sparseLongArray.size()


        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryLongalid) point++
            mEntryLongalid = true
            return sparseLongArray.keyAt(point)
        }

        override fun remove() {
            check(mEntryLongalid)
            sparseLongArray.removeAt(point)
            mEntryLongalid = false
        }
    }

    class KeySet(
        private val sparseLongArray: SparseLongArray,
    ) : KeySetI<Int> {

        override fun clear() = sparseLongArray.clear()

        override fun iterator() = KeyIterator(sparseLongArray)

        override fun remove(element: Int): Boolean {
            val index=sparseLongArray.indexOfKey(element)
            return if (index>0){
                sparseLongArray.removeAt(index)
                true
            } else false
        }

        override val size: Int get() = sparseLongArray.size()

        override fun contains(element: Int) = sparseLongArray.containsKey(element)

        override fun isEmpty() = sparseLongArray.isEmpty()

        override fun toString() = sparseLongArray.toString()

    }

    class EntryIterator(private val sparseLongArray: SparseLongArray) :
        MutableIterator<MutableMap.MutableEntry<Int, Long>> {
        private var point: Int = 0
        private var mEntryLongalid = false
        override fun hasNext() = point + (if (mEntryLongalid) 1 else 0) < sparseLongArray.size()

        override fun next(): MutableMap.MutableEntry<Int, Long> {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryLongalid) point++
            mEntryLongalid = true
            return InternalNode(sparseLongArray.keyAt(point), sparseLongArray)
        }

        override fun remove() {
            check(mEntryLongalid)
            sparseLongArray.removeAt(point)
            mEntryLongalid = false
        }

        private class InternalNode(
            private val k: Int,
            private val sparseLongArray: SparseLongArray,
        ) : NodeA<Int, Long>() {
            override val key: Int
                get() = k
            override val value: Long
                get() = sparseLongArray.get(k)

            override fun setValue(newValue: Long): Long {
                val old = value
                sparseLongArray.put(k, newValue)
                return old
            }
        }
    }


    class EntrySet(private val sparseLongArray: SparseLongArray) :
        EntrySetI<Int, Long> {
        override fun add(element: MutableMap.MutableEntry<Int, Long>): Boolean {
            val index = sparseLongArray.indexOfKey(element.key)
            if (index >= 0) if (sparseLongArray.valueAt(index) == element.value) return false
            sparseLongArray.put(element.key, element.value)
            return true

        }

        override fun clear() = sparseLongArray.clear()

        override fun iterator() = EntryIterator(sparseLongArray)

        override fun remove(element: MutableMap.MutableEntry<Int, Long>): Boolean {
            val k = element.key
            val index = sparseLongArray.indexOfKey(k)
            if (index >= 0) {
                if (sparseLongArray.valueAt(index) == element.value) {
                    sparseLongArray.removeAt(index)
                    return true
                }
            }
            return false
        }


        override val size: Int = sparseLongArray.size()

        override fun contains(element: MutableMap.MutableEntry<Int, Long>): Boolean {
            val index = sparseLongArray.indexOfKey(element.key)
            return if (index >= 0) sparseLongArray.valueAt(index) == element.value else false
        }

        override fun isEmpty() = sparseLongArray.isEmpty()

        override fun toString() = sparseLongArray.toString()
    }
}