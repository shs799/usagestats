package com.privacyFirst.usageStats.underconstruction

import androidx.collection.ArrayMap
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.collection.EmptyMutableMap
import java.util.concurrent.ConcurrentHashMap

class AutoSwitchMap<K, V> : Map<K, V> {
    constructor(m: MutableMap<K, V>) {
        this.mm = mm
    }

    private var chm: ConcurrentHashMap<K, V>? = null
    private var hm: HashMap<K, V>? = null
    private var am: ArrayMap<K, V>? = null
    private var mm: MutableMap<K, V>? = EmptyMutableMap()

    fun setMap(m: Map<K, V>) {
        val nchm = ConcurrentHashMap<K, V>(m.size)
        chm = nchm
        m.entries.parallelStream().forEach { i ->
            //nchm.put(i.key, i.value)
            TODO("need a nullable ConcurrentHashMap")
        }
    }

    override val size: Int
        get() = TODO("Not yet implemented")

    override fun containsKey(key: K): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsValue(value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun isEmpty() =
        size == 0

    override val entries: Set<Map.Entry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: Set<K>
        get() = TODO("Not yet implemented")
    override val values: Collection<V>
        get() = TODO("Not yet implemented")


}