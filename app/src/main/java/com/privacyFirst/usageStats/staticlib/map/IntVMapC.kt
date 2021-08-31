package com.privacyFirst.usageStats.staticlib.method

import androidx.collection.SparseArrayCompat
import java.util.*

class IntVMapC<V> : MutableMap<Int, V> {
    var sparseArrayCompat: SparseArrayCompat<V>? = null
        get() {
            //garbage collection
            field!!.size()
            return field
        }
        set(value) {
            //garbage collection
            value!!.size()
            field = value
        }

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
        get() = sparseArrayCompat!!.size()

    override fun containsKey(key: Int) = sparseArrayCompat!!.containsKey(key)
    override fun containsValue(value: V) = sparseArrayCompat!!.containsValue(value)
    override fun get(key: Int): V? = sparseArrayCompat!!.get(key)
    override fun isEmpty() = sparseArrayCompat!!.isEmpty

    override val entries: MutableSet<MutableMap.MutableEntry<Int, V>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val es = EntrySet(sparseArrayCompat!!)
                mEntrySet = es
                es
            } else {
                es
            }
        }

    private var mEntrySet: MutableSet<MutableMap.MutableEntry<Int, V>>? = null

    override val keys: MutableSet<Int>
        get() {
            val k = mKeys
            return if (k == null) {
                val k = KeySet(sparseArrayCompat!!)
                mKeys = k
                k
            } else {
                k
            }
        }
    private var mKeys: KeySet<V>? = null

    override val values: MutableCollection<V>
        get() {
            val v = mValues
            return if (v == null) {
                val v = ValueList(sparseArrayCompat!!)
                mValues = v
                v
            } else {
                v
            }
        }
    private var mValues: MutableCollection<V>? = null

    override fun clear() =
        sparseArrayCompat!!.clear()


    override fun put(key: Int, value: V): V? {
        val index = sparseArrayCompat!!.indexOfKey(key)
        return if (index >= 0) {
            val oldValue = sparseArrayCompat!!.valueAt(index)
            sparseArrayCompat!!.setValueAt(index, value)
            oldValue
        } else {
            sparseArrayCompat!!.put(key, value)
            null
        }
    }

    override fun putAll(from: Map<out Int, V>) {
        from.entries.forEach { i ->
            sparseArrayCompat!!.put(i.key, i.value)
        }
    }

    override fun remove(key: Int): V? {
        val index = sparseArrayCompat!!.indexOfKey(key)
        if (index < 0)
            return null
        val oldValue = sparseArrayCompat!!.valueAt(index)
        sparseArrayCompat!!.removeAt(index)
        return oldValue
    }


    override fun toString() =
        sparseArrayCompat!!.toString()


    class Node<V>(key: Int, value: V) : MutableMap.MutableEntry<Int, V> {
        override var key: Int = key
            private set
        override var value: V = value

        override fun setValue(newValue: V): V {
            val old = value
            value = newValue
            return old
        }

        override fun toString() =
            "$key=$value"

    }

    private class ValueIterator<V>(
        private var sparseArrayCompat: SparseArrayCompat<V>,
    ) :
        MutableIterator<V> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext() =
            point < sparseArrayCompat.size()


        override fun next(): V {
            if (!hasNext()) throw NoSuchElementException()
            //do not move pointer, if remove
            if (mEntryValid)
                point++
            mEntryValid = true
            return sparseArrayCompat.valueAt(point)
        }

        override fun remove() {
            check(mEntryValid)
            sparseArrayCompat.removeAt(point)
            mEntryValid = false
        }
    }

    private class ValueList<V>(
        private var sparseArrayCompat: SparseArrayCompat<V>,
    ) : MutableCollection<V> {
        override val size: Int
            get() = sparseArrayCompat.size()

        override fun contains(element: V) =
            sparseArrayCompat.containsValue(element)


        override fun containsAll(elements: Collection<V>): Boolean {
            if (this === elements) return true
            elements.forEach { e ->
                if (!contains(e)) return false
            }
            return true
        }

        override fun isEmpty() =
            sparseArrayCompat.isEmpty

        override fun iterator(): MutableIterator<V> {
            val v1 = mIterator
            return if (v1 == null) {
                val v2 = ValueIterator(sparseArrayCompat)
                mIterator = v2
                v2
            } else {
                v1
            }
        }

        private var mIterator: MutableIterator<V>? = null

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun add(element: V) =
            throw UnsupportedOperationException()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun addAll(elements: Collection<V>) =
            throw UnsupportedOperationException()


        override fun clear() =
            sparseArrayCompat.clear()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun remove(element: V) =
            throw UnsupportedOperationException()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun removeAll(elements: Collection<V>) =
            throw UnsupportedOperationException()

        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun retainAll(elements: Collection<V>) =
            throw UnsupportedOperationException()

        override fun toString() =
            sparseArrayCompat.toString()

    }

    private class KeyIterator<V>(
        private var sparseArrayCompat: SparseArrayCompat<V>,
    ) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext(): Boolean {
            return point < sparseArrayCompat.size()
        }

        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            if (!mEntryValid)
                point++
            mEntryValid = true
            return sparseArrayCompat.keyAt(point)
        }

        override fun remove() {
            check(mEntryValid)
            sparseArrayCompat.removeAt(point)
            mEntryValid = false
        }
    }

    private class KeySet<V>(
        private val sparseArrayCompat: SparseArrayCompat<V>,
    ) : MutableSet<Int> {
        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun add(element: Int) =
            throw UnsupportedOperationException()


        @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
        override fun addAll(elements: Collection<Int>) =
            throw UnsupportedOperationException()


        override fun clear() {
            sparseArrayCompat.clear()
        }

        override fun iterator(): MutableIterator<Int> {
            val v1 = mIterator
            return if (v1 == null) {
                val v2 = KeyIterator(sparseArrayCompat)
                mIterator = v2
                v2
            } else {
                v1
            }
        }

        private var mIterator: MutableIterator<Int>? = null
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

        override fun isEmpty() =
            sparseArrayCompat.isEmpty

        override fun toString() =
            sparseArrayCompat.toString()

    }


    private class MapIterator<V>(
        private var sparseArrayCompat: SparseArrayCompat<V>,
    ) :
        MutableIterator<MutableMap.MutableEntry<Int, V>> {
        private var point: Int = 0
        private var mEntryValid = false
        override fun hasNext(): Boolean {
            return point < sparseArrayCompat.size()
        }

        override fun next(): MutableMap.MutableEntry<Int, V> {
            if (!hasNext()) throw NoSuchElementException()
            if (!mEntryValid)
                point++
            mEntryValid = true
            return Node(sparseArrayCompat.keyAt(point), sparseArrayCompat.valueAt(point))
        }

        override fun remove() {
            check(mEntryValid)
            sparseArrayCompat.removeAt(point)
            mEntryValid = false
        }
    }

    private class EntrySet<V>(
        private val sparseArrayCompat: SparseArrayCompat<V>,
    ) :
        MutableSet<MutableMap.MutableEntry<Int, V>> {
        override fun add(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArrayCompat.indexOfKey(element.key)
            return if (index >= 0) {
                if (objectEqual(sparseArrayCompat.valueAt(index), element.value)) {
                    false
                } else {
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
            elements.forEach { i ->
                val success = add(i)
                if (success) modify = true
            }
            return modify
        }

        override fun clear() {
            sparseArrayCompat.clear()
        }

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<Int, V>> {
            val i = mIterator
            return if (i == null) {
                val newI = MapIterator(sparseArrayCompat)
                mIterator = newI
                newI
            } else {
                i
            }
        }

        private var mIterator: MutableIterator<MutableMap.MutableEntry<Int, V>>? = null

        override fun remove(element: MutableMap.MutableEntry<Int, V>): Boolean {
            val index = sparseArrayCompat.indexOfKey(element.key)
            if (index >= 0)
                if (objectEqual(sparseArrayCompat.valueAt(index), element.value)) {
                    sparseArrayCompat.removeAt(index)
                    return true
                }

            return false
        }

        override fun removeAll(elements: Collection<MutableMap.MutableEntry<Int, V>>): Boolean {
            var modify = false
            elements.forEach { i ->
                if (remove(i)) modify = true
            }
            return modify
        }


        private fun retainAllContain(
            elements: Collection<MutableMap.MutableEntry<Int, V>>,
            index: Int,
        ): Boolean {
            elements.forEach { n ->
                if ((n.key == sparseArrayCompat.keyAt(index)) && objectEqual(n.value,
                        sparseArrayCompat.valueAt(index))
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
                return objectEqual(sparseArrayCompat.valueAt(index), element.value)
            }
            return false
        }

        private fun objectEqual(o1: V, o2: V): Boolean {
            return (o1 === o2) || (o1 == o2)
        }

        override fun containsAll(elements: Collection<MutableMap.MutableEntry<Int, V>>): Boolean {
            if (this === elements) return true
            elements.forEach { e -> if (!contains(e)) return false }
            return true
        }

        override fun isEmpty() = sparseArrayCompat.isEmpty
        override fun toString() =
            sparseArrayCompat.toString()

    }
}