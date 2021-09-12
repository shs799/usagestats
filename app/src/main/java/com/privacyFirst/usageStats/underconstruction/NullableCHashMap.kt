package com.privacyFirst.usageStats.underconstruction

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class NullableCHashMap<K, V> : MutableMap<K, V>,ConcurrentMap<K,V> {

    private val mapKN = ConcurrentSkipListSet<K>()
    private val mapKV = ConcurrentHashMap<K, V>()
    private val mapNV = AtomicReference<V>()


    override val size: Int
        get() {
            val nullKeySize = if (mapNV.get() != null) 1 else 0
            return mapKV.size + mapKN.size + nullKeySize
        }


    override fun containsKey(key: K): Boolean {
        if (key == null)
            return mapNV.get() == null
        val a = CountDownLatch(1)//todo:Can be optimized
        var found = false
        val t1 = thread {
            val nv = mapKV.containsKey(key)
            if (nv) {
                found = true
                a.countDown()
            }
        }
        val t2 = thread {
            val contain = mapKN.contains(key)
            if (contain) {
                found = true
                a.countDown()
            }
        }
        t1.name = "finding in mapKV"
        t2.name = "finding in mapKN"
        t1.start()
        t2.start()
        a.await()
        t1.priority=Thread.MIN_PRIORITY
        t2.priority=Thread.MIN_PRIORITY
        return found
    }

    override fun containsValue(value: V): Boolean {
        TODO("Not yet implemented")
        if (value == null)
            return mapNV.get() === value

    }

    override fun get(key: K): V? {
        if (key == null)
            return mapNV.get()
        return mapKV[key]
    }

    override fun isEmpty() =
        size == 0


    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<K>
        get() = TODO("Not yet implemented")
    override val values: MutableCollection<V>
        get() = TODO("Not yet implemented")

    override fun clear() {
        mapKN.clear()
        mapKV.clear()
        mapNV.set(null)
    }

    override fun put(key: K, value: V): V? {
        TODO("Not yet implemented")
        if (key == null)
            return mapNV.getAndSet(value)
        val resultMapKN=mapKN.contains(key)
        val resultMapKV=mapKV.containsKey(key)
        if(value==null){

        }
        else{

        }
    }

    override fun putAll(from: Map<out K, V>) {
        from.entries.forEach { i ->
            put(i.key, i.value)
        }
    }

    override fun remove(key: K): V? {
        if (key == null) {
            val ret = mapNV.getAndSet(null)
            return ret
        }
            val a = CountDownLatch(1)//todo:Can be optimized
            var ret: V? = null
            val t1 = thread {
                val nv = mapKV.remove(key)
                    ret = nv
                    a.countDown()

            }
            val t2 = thread {
                val success = mapKN.remove(key)
                if (success) {
                    ret = null
                    a.countDown()
                }
            }

        t1.name = "finding in mapKV"
        t2.name = "finding in mapKN"
        t1.start()
        t2.start()
        a.await()
        t1.priority=Thread.MIN_PRIORITY
        t2.priority=Thread.MIN_PRIORITY
        return ret
    }

    override fun remove(key: K, value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun putIfAbsent(key: K, value: V): V? {
        TODO("Not yet implemented")
    }

    override fun replace(key: K, oldValue: V, newValue: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun replace(key: K, value: V): V? {
        TODO("Not yet implemented")
    }
}

