package com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal

internal interface MapCollectionI<E> : MutableCollection<E> {
    override fun containsAll(elements: Collection<E>): Boolean {
        if (this === elements) return true
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        if (this === elements) return false
        var modify = false
        elements.forEach { i -> if (add(i)) modify = true }
        return modify
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        if (this === elements) return false
        var modify = false
        val i = iterator()
        while (i.hasNext())
            if (!elements.contains(i.next())) {
                i.remove()
                modify = true
            }
        return modify
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        if (this === elements)
            return if (!isEmpty()) {
                clear()
                true
            } else false
        var modify = false
        elements.forEach { i -> if (remove(i)) modify = true }
        return modify
    }
}