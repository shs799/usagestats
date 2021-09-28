package com.privacyFirst.kotlin.infrastructure.lib.everywhere.collection

import java.util.*

class EmptyMutableMap<K, V> : MutableMap<K, V> {
    override val size: Int = 0
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val nes = EntrySet<K, V>()
                mEntrySet = nes
                nes
            } else es
        }
    private var mEntrySet: MutableSet<MutableMap.MutableEntry<K, V>>? = null
    override val keys: MutableSet<K>
        get() {
            val k = mKeys
            return if (k == null) {
                val nk = KeySet<K>()
                mKeys = nk
                nk
            } else k
        }
    private var mKeys: KeySet<K>? = null
    override val values: MutableCollection<V>
        get() {
            val v = mValues
            return if (v == null) {
                val nv = ValueList<V>()
                mValues = nv
                nv
            } else v
        }
    private var mValues: MutableCollection<V>? = null
    override fun containsKey(key: K) = false
    override fun containsValue(value: V) = false
    override fun get(key: K): V? = null
    override fun isEmpty() = true
    override fun clear() {}
    override fun put(key: K, value: V) = throw UnsupportedOperationException()
    override fun putAll(from: Map<out K, V>) = throw UnsupportedOperationException()
    override fun remove(key: K) = throw UnsupportedOperationException()
    private class EntrySetIterator<K, V> : MutableIterator<MutableMap.MutableEntry<K, V>> {
        override fun hasNext() = false
        override fun next() = throw NoSuchElementException()
        override fun remove() = throw UnsupportedOperationException()
    }

    private class KeySetIterator<K> : MutableIterator<K> {
        override fun hasNext() = false
        override fun next() = throw NoSuchElementException()
        override fun remove() = throw UnsupportedOperationException()
    }

    private class ValueListIterator<V> : MutableIterator<V> {
        override fun hasNext() = false
        override fun next() = throw NoSuchElementException()
        override fun remove() = throw UnsupportedOperationException()
    }

    private class EntrySet<K, V> : MutableSet<MutableMap.MutableEntry<K, V>> {
        override fun add(element: MutableMap.MutableEntry<K, V>) =
            throw UnsupportedOperationException()

        override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
            throw UnsupportedOperationException()

        override fun clear() {}
        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> = EntrySetIterator()
        override fun remove(element: MutableMap.MutableEntry<K, V>) =
            throw UnsupportedOperationException()

        override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
            throw UnsupportedOperationException()

        override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>) =
            throw UnsupportedOperationException()

        override val size = 0
        override fun contains(element: MutableMap.MutableEntry<K, V>) = false
        override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>) = false
        override fun isEmpty() = true
    }

    private class KeySet<K> : MutableSet<K> {
        override fun add(element: K) = throw UnsupportedOperationException()
        override fun addAll(elements: Collection<K>) = throw UnsupportedOperationException()
        override fun clear() {}
        override fun iterator(): MutableIterator<K> = KeySetIterator()
        override fun remove(element: K) = throw UnsupportedOperationException()
        override fun removeAll(elements: Collection<K>) = throw UnsupportedOperationException()
        override fun retainAll(elements: Collection<K>) = throw UnsupportedOperationException()
        override val size = 0
        override fun contains(element: K) = false
        override fun containsAll(elements: Collection<K>) = false
        override fun isEmpty() = true
    }

    private class ValueList<V> : MutableCollection<V> {
        override val size = 0
        override fun contains(element: V) = false
        override fun containsAll(elements: Collection<V>) = false
        override fun isEmpty() = true
        override fun add(element: V) = throw UnsupportedOperationException()
        override fun addAll(elements: Collection<V>) = throw UnsupportedOperationException()
        override fun clear() {}
        override fun iterator(): MutableIterator<V> = ValueListIterator()
        override fun remove(element: V) = throw UnsupportedOperationException()
        override fun removeAll(elements: Collection<V>) = throw UnsupportedOperationException()
        override fun retainAll(elements: Collection<V>) = throw UnsupportedOperationException()
    }

    override fun hashCode() = 0
    override fun equals(other: Any?) =
        when (other) {
            is EmptyMutableMap<*,*> -> true
            is Map<*, *> -> other.isEmpty()
            else -> false
        }
    override fun toString() = "{}"
}