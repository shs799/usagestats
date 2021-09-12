package com.privacyFirst.usageStats.dymaticlib.mapDowngrade

import com.privacyFirst.kotlin.infrastructure.lib.android.IntVMap
import java.util.concurrent.atomic.AtomicReference

/* It can convert map to ArrayMap to decrease memory-usage.
 * get value is available when converting.
 * writing map will wait until convert finish.
 *
 * Android Only
 */

//warning: Thread not-safe
class MapDowngradeIntV< V> : Map<Int, V> {
    private val a = AtomicReference<MutableMap<Int, V>>()

    private var removeSource = false
    private var thread: Thread? = null

    constructor(){
        a.set(IntVMap())
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

     fun getMap(): MutableMap<Int, V> {
        return a.get()
    }

    override val size: Int
        get() = getMap().size

    override fun containsKey(key: Int) =
        getMap().containsKey(key)


    override fun containsValue(value: V) = getMap().containsValue(value)


    override fun get(key: Int): V? = getMap()[key]


    override fun isEmpty() = getMap().isEmpty()

    override val entries: Set<Map.Entry<Int, V>>
        get() {

            return getMap().entries
        }

    override val keys: Set<Int>
        get() {

            return getMap().keys
        }
    override val values: Collection<V>
        get() {

            return getMap().values
        }
}