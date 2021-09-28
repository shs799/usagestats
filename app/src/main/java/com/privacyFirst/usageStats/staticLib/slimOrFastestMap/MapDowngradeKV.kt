package com.privacyFirst.usageStats.staticLib.slimOrFastestMap

import androidx.collection.ArrayMap
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.collection.EmptyMutableMap
import java.util.concurrent.atomic.AtomicReference


/* It can convert map to ArrayMap to decrease memory-usage.
 * get value is available when converting.
 * writing map will wait until convert finish.
 */

//warning: Thread not-safe
class MapDowngradeKV<K, V> : Map<K, V> {
    private val a = AtomicReference<MutableMap<K, V>>()

    var removeSource = false
    private var thread: Thread? = null

    constructor() {
        a.set(EmptyMutableMap())
    }

    constructor(from: MutableMap<K, V>) : this() {
        resetMap(from)
    }

    constructor(mc: MutableMap<K, V>, removeSource: Boolean) : this(mc) {
        this.removeSource = removeSource
    }

    fun resetMap(mc: MutableMap<K, V>) {
        a.set(mc)
        val t = Thread {
            val am = ArrayMap<K, V>(mc.size)
            am.putAll(mc)
            a.compareAndSet(mc, am)
            if (removeSource)
                mc.clear()
        }
        t.priority = Thread.MIN_PRIORITY
        t.name = "Converting Map"
        thread = t
        t.start()

    }

    private fun join() {
        thread?.join()
    }

    fun getMutableMap(): MutableMap<K, V> {
        join()
        return a.get()
    }

    private fun getMap(): MutableMap<K, V> {
        return a.get()
    }


    override val size: Int
        get() = getMap().size

    override fun containsKey(key: K) =
        getMap().containsKey(key)


    override fun containsValue(value: V) = getMap().containsValue(value)


    override fun get(key: K): V? = getMap()[key]


    override fun isEmpty() = getMap().isEmpty()

    override val entries =
        getMap().entries

    override val keys =
        getMap().keys

    override val values =
        getMap().values


}