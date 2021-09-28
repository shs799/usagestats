package com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal

internal interface KeySetI<K> :  MutableSet<K>, MapCollectionI<K> {
    @Deprecated("UnsupportedOperation", level = DeprecationLevel.HIDDEN)
    override fun add(element: K) = throw UnsupportedOperationException()

    @Deprecated("UnsupportedOperation", level = DeprecationLevel.HIDDEN)
    override fun addAll(elements: Collection<K>) = super.addAll(elements)

    override fun clear()

    override fun iterator(): MutableIterator<K>

    override fun remove(element: K): Boolean

    override fun removeAll(elements: Collection<K>): Boolean=super.removeAll(elements)

    override fun retainAll(elements: Collection<K>): Boolean=super.retainAll(elements)

    override val size: Int

    override fun contains(element: K): Boolean

    override fun containsAll(elements: Collection<K>): Boolean=super.containsAll(elements)

    override fun isEmpty(): Boolean

}