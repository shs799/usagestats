package com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal

internal interface ValueListI<V> :
    MapCollectionI<V> {
    @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
    override fun add(element: V) = throw UnsupportedOperationException()

    @Deprecated("UnsupportedOperationException", level = DeprecationLevel.HIDDEN)
    override fun addAll(elements: Collection<V>) = super.addAll(elements)

    override fun remove(element: V): Boolean {
        var modify = false
        val i = iterator()
        while (i.hasNext())
            if(i.next()==element) {
                i.remove()
                modify=true
            }
        return modify
    }
}