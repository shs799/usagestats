package com.privacyFirst.kotlin.infrastructure.lib.everywhere.collection

/*
IntVMap
IntVMap is a Integer-to-Object-Map based on SparseArrayCompat. It implemented the MutableMap interface, So you can downgrade to SparseArrayCompat faster.
It is based on AndroidX package, so it can run everywhere.
Iot of bug exist. Reporting are welcome.

//downgrade tutorial from ArrayMap to IntVMap
//from:
val map:MutableMap<Int,V>=ArrayMap<Int,V>()
//to:
val map:MutableMap<Int,V>=IntVMap<V>()

//bind exist SparseArrayCompat to MutableMap
val sparseArray=SparseArrayCompat()
val map:MutableMap<Int,V>=IntVMap<V>(sparseArray)
 */

//run everywhere

//warning: only get and getOrDefault method is thread safe

import androidx.collection.SparseArrayCompat
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.EntrySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.KeySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.NodeA
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.ValueListI
import java.util.*

class IntVMap<V> : MutableMap<Int, V> {
    var sparseArrayCompat: SparseArrayCompat<V>

    constructor() {
        sparseArrayCompat = SparseArrayCompat<V>()
    }

    constructor(size: Int) {
        sparseArrayCompat = SparseArrayCompat<V>(size)
    }

    constructor(sparseArrayCompat: SparseArrayCompat<V>) {
        this.sparseArrayCompat = sparseArrayCompat
    }

    override val size get() = sparseArrayCompat.size()
    override fun containsKey(key: Int) = sparseArrayCompat.containsKey(key)

    override fun containsValue(value: V) = sparseArrayCompat.containsValue(value)

    override fun get(key: Int): V? = sparseArrayCompat.get(key)

    override fun getOrDefault(key: Int, defaultValue: V): V =
        sparseArrayCompat.get(key, defaultValue)

    private fun exist(key: Int, notNullValue: V): Boolean {
        val v = Objects.requireNonNull(notNullValue)
        return if (get(key) == null) {
            if (getOrDefault(key, v) === v)
                false
            else false
        } else true
    }

    override fun isEmpty() = sparseArrayCompat.isEmpty
    override val entries: MutableSet<MutableMap.MutableEntry<Int, V>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val nes = EntrySet(sparseArrayCompat)
                mEntrySet = nes
                nes
            } else es
        }
    private var mEntrySet: MutableSet<MutableMap.MutableEntry<Int, V>>? = null

    override val keys: MutableSet<Int>
        get() {
            val k = mKeys
            return if (k == null) {
                val nk = KeySet(sparseArrayCompat)
                mKeys = nk
                nk
            } else k
        }
    private var mKeys: KeySet<V>? = null

    override val values: MutableCollection<V>
        get() {
            val v = mValues
            return if (v == null) {
                val nv = ValueList(sparseArrayCompat)
                mValues = nv
                nv
            } else v
        }
    private var mValues: MutableCollection<V>? = null
    override fun clear() = sparseArrayCompat.clear()

    override fun put(key: Int, value: V): V? {
        val old = get(key)
        sparseArrayCompat.put(key, value)
        return old
    }

    override fun putAll(from: Map<out Int, V>) =
        from.entries.forEach { i -> sparseArrayCompat.append(i.key, i.value) }

    override fun remove(key: Int): V? {
        val oldValue = get(key)
        sparseArrayCompat.remove(key)
        return oldValue
    }

    override fun remove(key: Int, value: V): Boolean {
        return sparseArrayCompat.remove(key, value)
    }

    override fun replace(key: Int, value: V): V? {
        return sparseArrayCompat.replace(key, value)
    }

    override fun replace(key: Int, oldValue: V, newValue: V): Boolean {
        return sparseArrayCompat.replace(key, oldValue, newValue)
    }

    override fun putIfAbsent(key: Int, value: V): V? {
        return sparseArrayCompat.putIfAbsent(key, value)
    }

    override fun toString() =
        sparseArrayCompat.toString()


    fun newNode(key: Int, value: V): MutableMap.MutableEntry<Int, V> {
        return Node(key, value)
    }

    private class Node<V>(override val key: Int, override var value: V) : NodeA<Int, V>() {
        override fun setValue(newValue: V): V {
            val old = value
            value = newValue
            return old
        }

        override fun toString() =
            "$key=$value"
    }

    class ValueIterator<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        MutableIterator<V> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < sparseArrayCompat.size()

        override fun remove() {
            check(mEntryValid)
            sparseArrayCompat.removeAt(point)
            mEntryValid = false
        }

        override fun next(): V {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return sparseArrayCompat.valueAt(point)
        }
    }

    class ValueList<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        ValueListI<V> {
        override val size: Int
            get() = sparseArrayCompat.size()

        override fun contains(element: V) = sparseArrayCompat.containsValue(element)

        override fun isEmpty() = sparseArrayCompat.isEmpty

        override fun iterator() = ValueIterator(sparseArrayCompat)

        override fun clear() = sparseArrayCompat.clear()

        override fun toString() = sparseArrayCompat.toString()

    }

    class KeyIterator<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() =
            point + (if (mEntryValid) 1 else 0) < sparseArrayCompat.size()


        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return sparseArrayCompat.keyAt(point)
        }

        override fun remove() {
            check(mEntryValid)
            sparseArrayCompat.removeAt(point)
            mEntryValid = false
        }
    }

    class KeySet<V>(
        private val sparseArrayCompat: SparseArrayCompat<V>,
    ) : KeySetI<Int> {

        override fun clear() = sparseArrayCompat.clear()

        override fun iterator() = KeyIterator(sparseArrayCompat)

        override fun remove(element: Int): Boolean {
            return if (contains(element)) {
                sparseArrayCompat.remove(element)
                true
            } else false
        }

        override val size: Int get() = sparseArrayCompat.size()

        override fun contains(element: Int) = sparseArrayCompat.containsKey(element)

        override fun isEmpty() = sparseArrayCompat.isEmpty

        override fun toString() = sparseArrayCompat.toString()

    }

    class EntryIterator<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        MutableIterator<MutableMap.MutableEntry<Int, V>> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < sparseArrayCompat.size()

        override fun next(): MutableMap.MutableEntry<Int, V> {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return InternalNode(point, sparseArrayCompat)
        }

        override fun remove() {
            check(mEntryValid)
            sparseArrayCompat.removeAt(point)
            mEntryValid = false
        }

        private class InternalNode<V>(
            private val index: Int,
            private val sparseArrayCompat: SparseArrayCompat<V>,
        ) : NodeA<Int, V>() {
            override val key: Int
                get() = sparseArrayCompat.keyAt(index)
            override val value: V
                get() = sparseArrayCompat.valueAt(index)

            override fun setValue(newValue: V): V {
                val old = value
                sparseArrayCompat.setValueAt(index,newValue)
                return old
            }
        }
    }


    class EntrySet<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        EntrySetI<Int, V> {
        override fun add(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArrayCompat.indexOfKey(element.key)
            return if (index >= 0) {
                if (sparseArrayCompat.valueAt(index) == element.value) false
                else {
                    sparseArrayCompat.setValueAt(index, element.value)
                    true
                }
            } else {
                sparseArrayCompat.put(element.key, element.value)
                true
            }
        }

        override fun clear() = sparseArrayCompat.clear()

        override fun iterator() = EntryIterator(sparseArrayCompat)

        override fun remove(element: MutableMap.MutableEntry<Int, V>) =
            sparseArrayCompat.remove(element.key, element.value)

        override val size: Int = sparseArrayCompat.size()

        override fun contains(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArrayCompat.indexOfKey(element.key)
            return if (index >= 0) sparseArrayCompat.valueAt(index) == element.value else false
        }

        override fun isEmpty() = sparseArrayCompat.isEmpty

        override fun toString() = sparseArrayCompat.toString()
    }
}