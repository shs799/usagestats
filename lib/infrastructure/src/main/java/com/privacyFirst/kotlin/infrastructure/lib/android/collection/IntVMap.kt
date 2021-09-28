package com.privacyFirst.kotlin.infrastructure.lib.android.collection

/*
IntVMap
IntVMap is a Integer-to-Object-Map based on SparseArray. It implemented the MutableMap interface, So you can downgrade to SparseArray faster.
It is based on AndroidX package, so it can run everywhere.
Iot of bug exist. Reporting are welcome.

//downgrade tutorial from ArrayMap to IntVMap
//from:
val map:MutableMap<Int,V>=ArrayMap<Int,V>()
//to:
val map:MutableMap<Int,V>=IntVMap<V>()

//bind exist SparseArray to MutableMap
val sparseArray=SparseArray()
val map:MutableMap<Int,V>=IntVMap<V>(sparseArray)
 */

//run everywhere

//warning: only get and getOrDefault method is thread safe

import android.os.Build
import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.core.util.containsValue
import androidx.core.util.isEmpty
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.EntrySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.KeySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.NodeA
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.ValueListI
import java.util.*

class IntVMap<V> : MutableMap<Int, V> {
    var sparseArray: SparseArray<V>

    constructor() {
        sparseArray = SparseArray<V>()
    }

    constructor(size: Int) {
        sparseArray = SparseArray<V>(size)
    }

    constructor(sparseArray: SparseArray<V>) {
        this.sparseArray = sparseArray
    }
    //safe
    override val size get() = sparseArray.size()
    override fun containsKey(key: Int) = sparseArray.containsKey(key)

    override fun containsValue(value: V) = sparseArray.containsValue(value)

    override fun get(key: Int): V? = sparseArray.get(key)

    override fun getOrDefault(key: Int, defaultValue: V): V =
        sparseArray.get(key, defaultValue)

    private fun exist(key: Int, notNullValue: V): Boolean {
        val v = Objects.requireNonNull(notNullValue)
        return if (get(key) == null) {
            if (getOrDefault(key, v) === v)
                false
            else false
        } else true
    }

    override fun isEmpty() = sparseArray.isEmpty()
    override val entries: MutableSet<MutableMap.MutableEntry<Int, V>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val nes = EntrySet(sparseArray)
                mEntrySet = nes
                nes
            } else es
        }
    private var mEntrySet: MutableSet<MutableMap.MutableEntry<Int, V>>? = null

    override val keys: MutableSet<Int>
        get() {
            val k = mKeys
            return if (k == null) {
                val nk = KeySet(sparseArray)
                mKeys = nk
                nk
            } else k
        }
    private var mKeys: KeySet<V>? = null

    override val values: MutableCollection<V>
        get() {
            val v = mValues
            return if (v == null) {
                val nv = ValueList(sparseArray)
                mValues = nv
                nv
            } else v
        }
    private var mValues: MutableCollection<V>? = null
    override fun clear() = sparseArray.clear()

    override fun put(key: Int, value: V): V? {
        val old = get(key)
        sparseArray.put(key, value)
        return old
    }

    override fun putAll(from: Map<out Int, V>) =
        from.entries.forEach { i -> sparseArray.append(i.key, i.value) }

    override fun remove(key: Int): V? {
        val oldValue = get(key)
        sparseArray.remove(key)
        return oldValue
    }

    override fun toString() =
        sparseArray.toString()


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

    class ValueIterator<V>(private val sparseArray: SparseArray<V>) :
        MutableIterator<V> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < sparseArray.size()

        override fun remove() {
            check(mEntryValid)
            IntVAddition.removeAt(sparseArray, point)
            mEntryValid = false
        }

        override fun next(): V {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return sparseArray.valueAt(point)
        }
    }

    class ValueList<V>(private val sparseArray: SparseArray<V>) :
        ValueListI<V> {
        override val size: Int
            get() = sparseArray.size()

        override fun contains(element: V) = sparseArray.containsValue(element)

        override fun isEmpty() = sparseArray.isEmpty()

        override fun iterator() = ValueIterator(sparseArray)

        override fun clear() = sparseArray.clear()

        override fun toString() = sparseArray.toString()

    }

    class KeyIterator<V>(private val sparseArray: SparseArray<V>) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() =
            point + (if (mEntryValid) 1 else 0) < sparseArray.size()


        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return sparseArray.keyAt(point)
        }

        override fun remove() {
            check(mEntryValid)
            IntVAddition.removeAt(sparseArray, point)
            mEntryValid = false
        }
    }

    class KeySet<V>(
        private val sparseArray: SparseArray<V>,
    ) : KeySetI<Int> {

        override fun clear() = sparseArray.clear()

        override fun iterator() = KeyIterator(sparseArray)

        override fun remove(element: Int): Boolean {
            return if (contains(element)) {
                sparseArray.remove(element)
                true
            } else false
        }

        override val size: Int get() = sparseArray.size()

        override fun contains(element: Int) = sparseArray.containsKey(element)

        override fun isEmpty() = sparseArray.isEmpty()

        override fun toString() = sparseArray.toString()

    }

    class EntryIterator<V>(private val sparseArray: SparseArray<V>) :
        MutableIterator<MutableMap.MutableEntry<Int, V>> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < sparseArray.size()

        override fun next(): MutableMap.MutableEntry<Int, V> {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return InternalNode(sparseArray.keyAt(point), sparseArray)
        }

        override fun remove() {
            check(mEntryValid)
            IntVAddition.removeAt(sparseArray, point)
            mEntryValid = false
        }

        private class InternalNode<V>(
            private val k: Int,
            private val sparseArray: SparseArray<V>,
        ) : NodeA<Int, V>() {
            override val key: Int
                get() = k
            override val value: V
                get() = sparseArray.get(k, null)

            override fun setValue(newValue: V): V {
                val old = value
                sparseArray.put(k, newValue)
                return old
            }
        }
    }


    class EntrySet<V>(private val sparseArray: SparseArray<V>) :
        EntrySetI<Int, V> {
        override fun add(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArray.indexOfKey(element.key)
            return if (index >= 0) {
                if (sparseArray.valueAt(index) == element.value) false
                else {
                    sparseArray.setValueAt(index, element.value)
                    true
                }
            } else {
                sparseArray.put(element.key, element.value)
                true
            }
        }

        override fun clear() = sparseArray.clear()

        override fun iterator() = EntryIterator(sparseArray)

        override fun remove(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val k = element.key
            val index = sparseArray.indexOfKey(k)
            if (index >= 0) {
                if (sparseArray.valueAt(index) == element.value) {
                    sparseArray.remove(k)
                    return true
                }
            }
            return false
        }


        override val size: Int = sparseArray.size()

        override fun contains(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArray.indexOfKey(element.key)
            return if (index >= 0) sparseArray.valueAt(index) == element.value else false
        }

        override fun isEmpty() = sparseArray.isEmpty()

        override fun toString() = sparseArray.toString()
    }
}

private object IntVAddition{
    fun <E> removeAt(sparseArray: SparseArray<E>, index: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) sparseArray.removeAt(index)
        else sparseArray.remove(sparseArray.keyAt(index))
    }
}