package com.privacyFirst.kotlin.infrastructure.lib.everywhere.collection

/*
LongVMap
LongVMap is a Longeger-to-Object-Map based on LongSparseArray. It implemented the MutableMap interface, So you can downgrade to LongSparseArray faster.
It is based on AndroidX package, so it can run everywhere.
Iot of bug exist. Reporting are welcome.

//downgrade tutorial from ArrayMap to LongVMap
//from:
val map:MutableMap<Long,V>=ArrayMap<Long,V>()
//to:
val map:MutableMap<Long,V>=LongVMap<V>()

//bind exist LongSparseArray to MutableMap
val sparseArray=LongSparseArray()
val map:MutableMap<Long,V>=LongVMap<V>(sparseArray)
 */

//run everywhere

//warning: only get and getOrDefault method is thread safe

import androidx.collection.LongSparseArray
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.EntrySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.KeySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.NodeA
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.ValueListI
import java.util.*

class LongVMap<V> : MutableMap<Long, V> {
    var longSparseArray: LongSparseArray<V>

    constructor() {
        longSparseArray = LongSparseArray<V>()
    }

    constructor(size: Int) {
        longSparseArray = LongSparseArray<V>(size)
    }

    constructor(longSparseArray: LongSparseArray<V>) {
        this.longSparseArray = longSparseArray
    }

    override val size get() = longSparseArray.size()
    override fun containsKey(key: Long) = longSparseArray.containsKey(key)

    override fun containsValue(value: V) = longSparseArray.containsValue(value)

    override fun get(key: Long): V? = longSparseArray.get(key)

    override fun getOrDefault(key: Long, defaultValue: V): V =
        longSparseArray.get(key, defaultValue)

    private fun exist(key: Long, notNullValue: V): Boolean {
        val v = Objects.requireNonNull(notNullValue)
        return if (get(key) == null) {
            if (getOrDefault(key, v) === v)
                false
            else false
        } else true
    }

    override fun isEmpty() = longSparseArray.isEmpty
    override val entries: MutableSet<MutableMap.MutableEntry<Long, V>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val nes = EntrySet(longSparseArray)
                mEntrySet = nes
                nes
            } else es
        }
    private var mEntrySet: MutableSet<MutableMap.MutableEntry<Long, V>>? = null

    override val keys: MutableSet<Long>
        get() {
            val k = mKeys
            return if (k == null) {
                val nk = KeySet(longSparseArray)
                mKeys = nk
                nk
            } else k
        }
    private var mKeys: KeySet<V>? = null

    override val values: MutableCollection<V>
        get() {
            val v = mValues
            return if (v == null) {
                val nv = ValueList(longSparseArray)
                mValues = nv
                nv
            } else v
        }
    private var mValues: MutableCollection<V>? = null
    override fun clear() = longSparseArray.clear()

    override fun put(key: Long, value: V): V? {
        val old = get(key)
        longSparseArray.put(key, value)
        return old
    }

    override fun putAll(from: Map<out Long, V>) =
        from.entries.forEach { i -> longSparseArray.append(i.key, i.value) }

    override fun remove(key: Long): V? {
        val oldValue = get(key)
        longSparseArray.remove(key)
        return oldValue
    }

    override fun remove(key: Long, value: V): Boolean {
        return longSparseArray.remove(key, value)
    }

    override fun replace(key: Long, value: V): V? {
        return longSparseArray.replace(key, value)
    }

    override fun replace(key: Long, oldValue: V, newValue: V): Boolean {
        return longSparseArray.replace(key, oldValue, newValue)
    }

    override fun putIfAbsent(key: Long, value: V): V? {
        return longSparseArray.putIfAbsent(key, value)
    }

    override fun toString() =
        longSparseArray.toString()


    fun newNode(key: Long, value: V): MutableMap.MutableEntry<Long, V> {
        return Node(key, value)
    }

    private class Node<V>(override val key: Long, override var value: V) : NodeA<Long, V>() {
        override fun setValue(newValue: V): V {
            val old = value
            value = newValue
            return old
        }

        override fun toString() =
            "$key=$value"
    }

    class ValueIterator<V>(private val longSparseArray: LongSparseArray<V>) :
        MutableIterator<V> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < longSparseArray.size()

        override fun remove() {
            check(mEntryValid)
            longSparseArray.removeAt(point)
            mEntryValid = false
        }

        override fun next(): V {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return longSparseArray.valueAt(point)
        }
    }

    class ValueList<V>(private val longSparseArray: LongSparseArray<V>) :
        ValueListI<V> {
        override val size: Int
            get() = longSparseArray.size()

        override fun contains(element: V) = longSparseArray.containsValue(element)

        override fun isEmpty() = longSparseArray.isEmpty

        override fun iterator() = ValueIterator(longSparseArray)

        override fun clear() = longSparseArray.clear()

        override fun toString() = longSparseArray.toString()

    }

    class KeyIterator<V>(private val longSparseArray: LongSparseArray<V>) :
        MutableIterator<Long> {
        private var point: Int = 0
        private var mEntryValid = false
        private var key:Long=0
        override fun hasNext() =
            point + (if (mEntryValid) 1 else 0) < longSparseArray.size()


        override fun next(): Long {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            key=longSparseArray.keyAt(point)
            return key
        }

        override fun remove() {
            check(mEntryValid)
            check(longSparseArray.containsKey(key))
            longSparseArray.remove(key)
            mEntryValid = false
        }
    }

    class KeySet<V>(
        private val longSparseArray: LongSparseArray<V>,
    ) : KeySetI<Long> {

        override fun clear() = longSparseArray.clear()

        override fun iterator() = KeyIterator(longSparseArray)

        override fun remove(element: Long): Boolean {
            return if (contains(element)) {
                longSparseArray.remove(element)
                true
            } else false
        }

        override val size: Int get() = longSparseArray.size()

        override fun contains(element: Long) = longSparseArray.containsKey(element)

        override fun isEmpty() = longSparseArray.isEmpty

        override fun toString() = longSparseArray.toString()

    }

    class EntryIterator<V>(private val longSparseArray: LongSparseArray<V>) :
        MutableIterator<MutableMap.MutableEntry<Long, V>> {
        private var point: Int = 0
        private var mEntryValid = false
        private var key:Long=0
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < longSparseArray.size()

        override fun next(): MutableMap.MutableEntry<Long, V> {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            key=longSparseArray.keyAt(point)
            return InternalNode(key, longSparseArray)
        }

        override fun remove() {
            check(mEntryValid)
            check(longSparseArray.containsKey(key))
            longSparseArray.removeAt(point)
            mEntryValid = false
        }

        private class InternalNode<V>(
            private val k: Long,
            private val longSparseArray: LongSparseArray<V>,
        ) : NodeA<Long, V>() {
            override val key: Long
                get() = k
            override val value: V
                get() = longSparseArray.get(k,null)

            override fun setValue(newValue: V): V {
                val old = value
                longSparseArray.put(k,newValue)
                return old
            }
        }
    }


    class EntrySet<V>(private val longSparseArray: LongSparseArray<V>) :
        EntrySetI<Long, V> {
        override fun add(element: MutableMap.MutableEntry<Long, V>): Boolean {
            val index = longSparseArray.indexOfKey(element.key)
            return if (index >= 0) {
                if (longSparseArray.valueAt(index) == element.value) false
                else {
                    longSparseArray.setValueAt(index, element.value)
                    true
                }
            } else {
                longSparseArray.put(element.key, element.value)
                true
            }
        }

        override fun clear() = longSparseArray.clear()

        override fun iterator() = EntryIterator(longSparseArray)

        override fun remove(element: MutableMap.MutableEntry<Long, V>) =
            longSparseArray.remove(element.key, element.value)

        override val size: Int = longSparseArray.size()

        override fun contains(element: MutableMap.MutableEntry<Long, V>): Boolean {
            val index = longSparseArray.indexOfKey(element.key)
            return if (index >= 0) longSparseArray.valueAt(index) == element.value else false
        }

        override fun isEmpty() = longSparseArray.isEmpty

        override fun toString() = longSparseArray.toString()
    }
}