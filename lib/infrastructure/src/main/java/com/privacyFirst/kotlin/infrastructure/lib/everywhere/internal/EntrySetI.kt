package com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal

internal interface EntrySetI<K, V> : MutableSet<MutableMap.MutableEntry<K, V>>,
    MapCollectionI<MutableMap.MutableEntry<K, V>> {
    override fun add(element: MutableMap.MutableEntry<K, V>): Boolean

    override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
        super.addAll(elements)

    override fun clear()

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>>

    override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean

    override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
        super.removeAll(elements)

    override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
        super.retainAll(elements)

    override val size: Int

    override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean

    override fun containsAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean =
        super.containsAll(elements)

    override fun isEmpty(): Boolean
}