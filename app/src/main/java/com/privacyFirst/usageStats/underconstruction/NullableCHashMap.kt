package com.privacyFirst.usageStats.underconstruction


import com.privacyFirst.usageStats.staticLib.concurrent.AtomicReferenceKT
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class NullableCHashMap<K, V> : ConcurrentMap<K, V> {

    private val mapK = ConcurrentHashMap<K, MapNode<V>>()
    private val mapN = AtomicReferenceKT<MapNode<V>?>()


    override val size: Int
        get() = mapK.size + if (mapN.get() != null) 1 else 0



    override fun containsKey(key: K): Boolean {
        if (key == null) {
            if (mapN.get() != null) return true
        } else {
            if (mapK.containsKey(key)) return true
        }
        return false
    }

    override fun containsValue(value: V): Boolean {
        val n = MapNode(value)
        if (value == null) {
            if (mapN.get() == n) return true
        } else {
            if (mapK.containsValue(n)) return true
        }
        return false
    }

    override fun get(key: K): V? {
        return if (key == null) mapN.get()?.v else mapK[key]?.v
    }

    override fun isEmpty(): Boolean {
        return when {
            mapN.get() != null -> false
            else -> mapK.isEmpty()
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<K>
        get() = TODO("Not yet implemented")
    override val values: MutableCollection<V>
        get() = TODO("Not yet implemented")

    override fun clear() {
        mapK.clear()
        mapN.set(null)
    }

    override fun put(key: K, value: V): V? {
        return if (key == null) {
            val old = mapN.getAndSet(MapNode(value))
            old?.v
        } else {
            val new = MapNode(value)
            mapK.put(key, new)?.v
        }
    }

    override fun putAll(from: Map<out K, V>) {
        from.entries.forEach { i ->
            put(i.key, i.value)
        }
    }

    override fun remove(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun remove(key: K, value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun putIfAbsent(key: K, value: V): V? {
        TODO("Not yet implemented")
    }

    override fun replace(key: K, oldValue: V, newValue: V): Boolean {
        return if (key == null) {
            if (mapN.get() != null) {
                mapN.set(MapNode(newValue))
                true
            } else false
        } else mapK.replace(key, MapNode(oldValue), MapNode(newValue))

    }

    override fun replace(key: K, value: V): V? {
        TODO("Not yet implemented")
    }
}

private class MapNode<V>(val v: V) {
    override fun equals(other: Any?) = v == other
    override fun hashCode() = v?.hashCode() ?: 0
    override fun toString() = v.toString()
}