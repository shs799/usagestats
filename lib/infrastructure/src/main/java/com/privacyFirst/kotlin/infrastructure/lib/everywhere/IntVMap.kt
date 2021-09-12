package com.privacyFirst.kotlin.infrastructure.lib.everywhere

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

//warning: Thread not-safe

import androidx.collection.SparseArrayCompat
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

    override val size
        get() = sparseArrayCompat.size()

    override fun containsKey(key: Int) = sparseArrayCompat.containsKey(key)
    override fun containsValue(value: V) = sparseArrayCompat.containsValue(value)
    override fun get(key: Int): V? = sparseArrayCompat.get(key)
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
        val index = sparseArrayCompat.indexOfKey(key)
        return if (index >= 0) {
            val oldValue = sparseArrayCompat.valueAt(index)
            sparseArrayCompat.setValueAt(index, value)
            oldValue
        } else {
            sparseArrayCompat.put(key, value)
            null
        }
    }

    override fun putAll(from: Map<out Int, V>) {
        from.entries.forEach { i -> sparseArrayCompat.put(i.key, i.value) }
    }

    override fun remove(key: Int): V? {
        val index = sparseArrayCompat.indexOfKey(key)
        if (index < 0)
            return null
        val oldValue = sparseArrayCompat.valueAt(index)
        sparseArrayCompat.removeAt(index)
        return oldValue
    }


    override fun toString() =
        sparseArrayCompat.toString()

    class Node<V>(override val key: Int, override var value: V) : MutableMap.MutableEntry<Int, V> {
        override fun setValue(newValue: V): V {
            val old = value
            value = newValue
            return old
        }

        override fun toString() =
            "$key=$value"
    }



    class ValueIterator<V>(private var sparseArrayCompat: SparseArrayCompat<V>) :
        MutableIterator<V> {
        private var point = 0
        private var mEntryValid = false
        override fun hasNext() =
            point+(if (mEntryValid)1 else 0) < sparseArrayCompat.size()

        override fun next(): V {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            return sparseArrayCompat.valueAt(point)
        }

        override fun remove() {
            check(mEntryValid)
            sparseArrayCompat.removeAt(point)
            mEntryValid = false
        }
    }

    class ValueList<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        MutableCollection<V> {
        override val size: Int
            get() = sparseArrayCompat.size()

        override fun contains(element: V) = sparseArrayCompat.containsValue(element)


        override fun containsAll(elements: Collection<V>): Boolean {
            if (this === elements) return true
            elements.forEach { e ->
                if (!contains(e)) return false
            }
            return true
        }

        override fun isEmpty() = sparseArrayCompat.isEmpty

        override fun iterator(): ValueIterator<V> {
            return ValueIterator(sparseArrayCompat)
        }


        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun add(element: V) =
            throw UnsupportedOperationException()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun addAll(elements: Collection<V>) =
            throw UnsupportedOperationException()

        override fun clear() = sparseArrayCompat.clear()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun remove(element: V) =
            throw UnsupportedOperationException()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun removeAll(elements: Collection<V>) =
            throw UnsupportedOperationException()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun retainAll(elements: Collection<V>) =
            throw UnsupportedOperationException()

        override fun toString() = sparseArrayCompat.toString()

    }

    class KeyIterator<V>(private var sparseArrayCompat: SparseArrayCompat<V>) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext()=
            point+(if (mEntryValid)1 else 0) < sparseArrayCompat.size()


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
    ) : MutableSet<Int> {
        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun add(element: Int) =
            throw UnsupportedOperationException()


        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun addAll(elements: Collection<Int>) =
            throw UnsupportedOperationException()


        override fun clear() = sparseArrayCompat.clear()


        override fun iterator(): KeyIterator<V> {
            return KeyIterator(sparseArrayCompat)
        }

        override fun remove(element: Int): Boolean {
            val index = sparseArrayCompat.indexOfKey(element)
            if (index >= 0) {
                sparseArrayCompat.removeAt(index)
                return true
            }
            return false
        }

        override fun removeAll(elements: Collection<Int>): Boolean {
            var modify = false
            elements.forEach { i ->
                if (remove(i)) modify = true
            }
            return modify
        }

        private fun retainAllContain(elements: Collection<Int>, index: Int): Boolean {
            elements.forEach { n ->
                if (n == sparseArrayCompat.keyAt(index))
                    return true
            }
            return false
        }

        override fun retainAll(elements: Collection<Int>): Boolean {
            var modify = false
            if (this === elements) return modify
            var deleteOffset = 0
            for (i in 0 until sparseArrayCompat.size() - deleteOffset) {
                val contain = retainAllContain(elements, i)
                if (contain) {
                    sparseArrayCompat.removeAt(i)
                    modify = true
                    deleteOffset++
                }
            }
            return modify
        }

        override val size: Int
            get() = sparseArrayCompat.size()

        override fun contains(element: Int) =
            sparseArrayCompat.containsKey(element)


        override fun containsAll(elements: Collection<Int>): Boolean {
            if (this === elements) return true
            elements.forEach { e ->
                if (!contains(e)) return false
            }
            return true
        }

        override fun isEmpty() = sparseArrayCompat.isEmpty

        override fun toString() = sparseArrayCompat.toString()

    }


    class PairIterator<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        MutableIterator<MutableMap.MutableEntry<Int, V>> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() = point+(if (mEntryValid)1 else 0) < sparseArrayCompat.size()

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
        ) : MutableMap.MutableEntry<Int, V> {
            override val key: Int
                get() = sparseArrayCompat.keyAt(index)
            override val value: V
                get() = sparseArrayCompat.valueAt(index)

            override fun setValue(newValue: V): V {
                val old = value
                sparseArrayCompat.setValueAt(index, newValue)
                return old
            }
            override fun toString() = "$key=$value"
        }
    }


    class EntrySet<V>(private val sparseArrayCompat: SparseArrayCompat<V>) :
        MutableSet<MutableMap.MutableEntry<Int, V>> {
        override fun add(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArrayCompat.indexOfKey(element.key)
            return if (index >= 0) {
                if (sparseArrayCompat.valueAt(index) === element.value) false
                else {
                    sparseArrayCompat.setValueAt(index, element.value)
                    true
                }
            } else {
                sparseArrayCompat.put(element.key, element.value)
                true
            }
        }

        override fun addAll(elements: Collection<MutableMap.MutableEntry<Int, V>>): Boolean {
            var modify = false
            if (this === elements) return modify
            elements.forEach { i -> if (add(i)) modify = true }
            return modify
        }

        override fun clear() = sparseArrayCompat.clear()

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<Int, V>> {
            return PairIterator(sparseArrayCompat)
        }

        override fun remove(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArrayCompat.indexOfKey(element.key)
            if (index >= 0)
                if (sparseArrayCompat.valueAt(index) === element.value) {
                    sparseArrayCompat.removeAt(index)
                    return true
                }
            return false
        }

        override fun removeAll(elements: Collection<MutableMap.MutableEntry<Int, V>>): Boolean {
            if (this === elements)
                return if (!sparseArrayCompat.isEmpty) {
                    sparseArrayCompat.clear()
                    true
                } else false
            var modify = false
            elements.forEach { i -> if (remove(i)) modify = true }
            return modify
        }


        private fun retainAllContain(
            elements: Collection<MutableMap.MutableEntry<Int, V>>, index: Int,
        ): Boolean {
            elements.forEach { n ->
                if (
                    (n.key == sparseArrayCompat.keyAt(index))
                    &&
                    (n.value === sparseArrayCompat.valueAt(index))
                )
                    return true
            }
            return false
        }

        override fun retainAll(elements: Collection<MutableMap.MutableEntry<Int, V>>): Boolean {
            var modify = false
            if (this === elements) return modify
            var deleteOffset = 0
            for (i in 0 until sparseArrayCompat.size() - deleteOffset) {
                val contain = retainAllContain(elements, i)
                if (contain) {
                    sparseArrayCompat.removeAt(i)
                    modify = true
                    deleteOffset++
                }
            }
            return modify
        }

        override val size: Int = sparseArrayCompat.size()
        override fun contains(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArrayCompat.indexOfKey(element.key)
            if (index >= 0) {
                return sparseArrayCompat.valueAt(index) === element.value
            }
            return false
        }

        override fun containsAll(elements: Collection<MutableMap.MutableEntry<Int, V>>): Boolean {
            if (this === elements) return true
            elements.forEach { e -> if (!contains(e)) return false }
            return true
        }

        override fun isEmpty() = sparseArrayCompat.isEmpty

        override fun toString() = sparseArrayCompat.toString()

    }
}