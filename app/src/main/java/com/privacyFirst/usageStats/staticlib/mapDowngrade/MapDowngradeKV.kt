package com.privacyFirst.usageStats.staticlib.mapDowngrade


/* It can convert map to ArrayMap to decrease memory-usage.
 * get value is available when converting.
 * writing map will wait until convert finish.
 */

//warning: Thread not-safe
class MapDowngradeKV<K, V>() : Map<K, V> {
    private var a : MutableMap<K, V>?=null

    private var removeSource = false
    private var thread: Thread? = null

    constructor(from: MutableMap<K, V>) : this() {
        resetMap(from)
    }

    constructor(mc: MutableMap<K, V>, removeSource: Boolean) : this(mc) {
        this.removeSource = removeSource
    }

    fun resetMap(mc: MutableMap<K, V>) {
        a=mc
        val t = Thread {
            val am = HashMap<K, V>(mc.size)
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

    fun getMap(): MutableMap<K, V> {
        thread?.join()
        return a!!
    }


    override val size: Int
        get() = a!!.size

    override fun containsKey(key: K) =
        a!!.containsKey(key)


    override fun containsValue(value: V) = a!!.containsValue(value)


    override fun get(key: K): V? = a!![key]


    override fun isEmpty() = a!!.isEmpty()

    override val entries =
        a!!.entries

    override val keys =
        a!!.keys

    override val values =
        a!!.values


}