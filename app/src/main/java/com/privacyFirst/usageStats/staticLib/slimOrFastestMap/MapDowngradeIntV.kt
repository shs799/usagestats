package com.privacyFirst.usageStats.staticLib.slimOrFastestMap

import com.privacyFirst.kotlin.infrastructure.lib.android.collection.IntVMap
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.collection.EmptyMutableMap
import java.util.concurrent.atomic.AtomicReference

/* It can convert map to ArrayMap to decrease memory-usage.
 * get value is available when converting.
 * writing map will wait until convert finish.
 *
 * run everywhere
 */

//warning: Thread not-safe
class MapDowngradeIntV<V> : Map<Int, V> {

    private val a = AtomicReference<MutableMap<Int, V>>()

    var removeSource = false
    private var thread: Thread? = null

    constructor() {
        a.set(EmptyMutableMap())
    }


    constructor(from: MutableMap<Int, V>) : this() {
        resetMap(from)
    }

    constructor(mc: MutableMap<Int, V>, removeSource: Boolean) : this(mc) {
        this.removeSource = removeSource
    }

    fun resetMap(mc: MutableMap<Int, V>) {
        a.set(mc)
        val t = Thread {
            val am = IntVMap<V>(mc.size)
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

    private fun getMap(): Map<Int, V> = a.get()

    fun getMutableMap() {
        join()
        a.get()
    }

    override val size: Int
        get() = getMap().size

    override fun containsKey(key: Int) =
        getMap().containsKey(key)

    override fun containsValue(value: V) = getMap().containsValue(value)

    override fun get(key: Int): V? = getMap()[key]

    override fun isEmpty() = getMap().isEmpty()

    override val entries: Set<Map.Entry<Int, V>>
        get() = getMap().entries


    override val keys: Set<Int>
        get() = getMap().keys

    override val values: Collection<V>
        get() = getMap().values

}