package com.privacyFirst.usageStats.staticlib.method

import java.util.concurrent.atomic.AtomicReference

/* It can convert map to ArrayMap to decrease memory-usage.
 * get value is available when converting.
 * writing map will wait until convert finish.
 *
 * Android only.
 */

//warning: Thread not-safe
class MapDowngradeIntV< V>() : Map<Int, V> {
    private var a : MutableMap<Int, V>?=null

    private var removeSource = false
    private var thread: Thread? = null

    constructor(from: MutableMap<Int, V>) : this() {
        resetMap(from)
    }

    constructor(mc: MutableMap<Int, V>, removeSource: Boolean) : this(mc) {
        this.removeSource = removeSource
    }

    private fun resetMap(mc: MutableMap<Int, V>) {
        a=mc
        val t = Thread {
            val am = android.util.ArrayMap<Int, V>(mc.size)
            am.putAll(mc)
            a=am
            if (removeSource)
                mc.clear()
        }
        t.priority = Thread.MIN_PRIORITY
        t.name = "Converting Map"
        thread = t
        t.start()

    }

    fun getMap(): MutableMap<Int, V> {
        thread?.join()
        return a!!
    }


    override val size: Int
        get() = a!!.size

    override fun containsKey(key: Int) =
        a!!.containsKey(key)


    override fun containsValue(value: V) = a!!.containsValue(value)


    override fun get(key: Int): V? = a!![key]


    override fun isEmpty() = a!!.isEmpty()

    override val entries =
        a!!.entries

    override val keys =
        a!!.keys

    override val values =
        a!!.values


}