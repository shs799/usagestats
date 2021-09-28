package com.privacyFirst.kotlin.infrastructure.lib.android.collection

/*
IntBooleanMap
IntBooleanMap is a Integer-to-Object-Map based on SparseBooleanArray. It implemented the MutableMap interface, So you can downgrade to SparseBooleanArray faster.
It is based on AndroidX package, so it can run everywhere.
Iot of bug exist. Reporting are welcome.

//downgrade tutorial from ArrayMap to IntBooleanMap
//from:
val map:MutableMap<Int,Boolean>=ArrayMap<Int,Boolean>()
//to:
val map:MutableMap<Int,Boolean>=IntBooleanMap<Boolean>()

//bind exist SparseBooleanArray to MutableMap
val sparseBooleanArray=SparseBooleanArray()
val map:MutableMap<Int,Boolean>=IntBooleanMap<Boolean>(sparseBooleanArray)
 */

//run everywhere

//warning: thread not-safe

import android.os.Build
import android.util.SparseBooleanArray
import androidx.annotation.RequiresApi
import androidx.core.util.containsKey
import androidx.core.util.containsValue
import androidx.core.util.isEmpty
import androidx.core.util.remove
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.EntrySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.KeySetI
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.NodeA
import com.privacyFirst.kotlin.infrastructure.lib.everywhere.internal.ValueListI
import java.util.*

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class IntBoolMap : MutableMap<Int, Boolean> {
    var sparseBooleanArray: SparseBooleanArray


    constructor() {
        sparseBooleanArray = SparseBooleanArray()
    }

    constructor(size: Int) {
        sparseBooleanArray = SparseBooleanArray(size)
    }

    constructor(sparseBooleanArray: SparseBooleanArray) {
        this.sparseBooleanArray = sparseBooleanArray
    }

    override val size get() = sparseBooleanArray.size()
    override fun containsKey(key: Int) = sparseBooleanArray.containsKey(key)
    override fun containsValue(value: Boolean) = sparseBooleanArray.containsValue(value)
    override fun get(key: Int) = IntBoolAddition.get(sparseBooleanArray, key)
    override fun getOrDefault(key: Int, defaultValue: Boolean): Boolean =
        sparseBooleanArray.get(key, defaultValue)

    override fun isEmpty() = sparseBooleanArray.isEmpty()
    override val entries: MutableSet<MutableMap.MutableEntry<Int, Boolean>>
        get() {
            val es = mEntrySet
            return if (es == null) {
                val nes = EntrySet(sparseBooleanArray)
                mEntrySet = nes
                nes
            } else es
        }
    private var mEntrySet: MutableSet<MutableMap.MutableEntry<Int, Boolean>>? = null

    override val keys: MutableSet<Int>
        get() {
            val k = mKeys
            return if (k == null) {
                val nk = KeySet(sparseBooleanArray)
                mKeys = nk
                nk
            } else k
        }
    private var mKeys: KeySet? = null

    override val values: MutableCollection<Boolean>
        get() {
            val v = mValues
            return if (v == null) {
                val nv = ValueList(sparseBooleanArray)
                mValues = nv
                nv
            } else v
        }
    private var mValues: MutableCollection<Boolean>? = null
    override fun clear() = sparseBooleanArray.clear()

    override fun put(key: Int, value: Boolean)=
        IntBoolAddition.put(sparseBooleanArray, key, value)


    override fun putAll(from: Map<out Int, Boolean>) =
        from.entries.forEach { i -> sparseBooleanArray.append(i.key, i.value) }

    override fun remove(key: Int): Boolean? {
        val oldValue = get(key)
        if (oldValue != null) sparseBooleanArray.remove(key, oldValue)
        return oldValue
    }

    override fun toString() =
        sparseBooleanArray.toString()


    fun newNode(key: Int, value: Boolean): MutableMap.MutableEntry<Int, Boolean> {
        return Node(key, value)
    }

    private class Node(override val key: Int, override var value: Boolean) :
        NodeA<Int, Boolean>() {
        override fun setValue(newValue: Boolean): Boolean {
            val old = value
            value = newValue
            return old
        }

        override fun toString() =
            "$key=$value"
    }

    class ValueIterator(private val sparseBooleanArray: SparseBooleanArray) :
        MutableIterator<Boolean> {
        private var mEntryValid = false
        private var key: Int = 0
        override fun hasNext() =
            sparseBooleanArray.indexOfKey(key) + (if (mEntryValid) 1 else 0) < sparseBooleanArray.size()

        override fun remove() {
            check(mEntryValid)
            val index = sparseBooleanArray.indexOfKey(key)
            if (index >= 0) IntBoolAddition.removeAt(sparseBooleanArray, index)
            else throw IllegalStateException()
            mEntryValid = false
        }

        override fun next(): Boolean {
            if (!hasNext()) throw NoSuchElementException()
            var index = sparseBooleanArray.indexOfKey(key)
            if (mEntryValid) index++
            mEntryValid = true
            val k = sparseBooleanArray.keyAt(index)
            key = k
            return sparseBooleanArray.valueAt(index)
        }
    }

    class ValueList(private val sparseBooleanArray: SparseBooleanArray) :
        ValueListI<Boolean> {
        override val size: Int
            get() = sparseBooleanArray.size()

        override fun contains(element: Boolean) = sparseBooleanArray.containsValue(element)
        override fun isEmpty() = sparseBooleanArray.isEmpty()
        override fun iterator() = ValueIterator(sparseBooleanArray)
        override fun clear() = sparseBooleanArray.clear()
        override fun toString() = sparseBooleanArray.toString()
    }

    class KeyIterator(private val sparseBooleanArray: SparseBooleanArray) :
        MutableIterator<Int> {
        private var point: Int = 0
        private var mEntryValid = false
        private var key: Int = 0
        override fun hasNext() =
            point + (if (mEntryValid) 1 else 0) < sparseBooleanArray.size()

        override fun next(): Int {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            val k = sparseBooleanArray.keyAt(point)
            key = k
            return k
        }

        override fun remove() {
            check(mEntryValid)
            val index = sparseBooleanArray.indexOfKey(key)
            check(index >= 0)
            if (index >= 0) IntBoolAddition.removeAt(sparseBooleanArray, index)
            mEntryValid = false
        }
    }

    class KeySet(
        private val sparseBooleanArray: SparseBooleanArray,
    ) : KeySetI<Int> {

        override fun clear() = sparseBooleanArray.clear()

        override fun iterator() = KeyIterator(sparseBooleanArray)

        override fun remove(element: Int): Boolean {
            val index = sparseBooleanArray.indexOfKey(element)
            return if (index > 0) {
                IntBoolAddition.removeAt(sparseBooleanArray, index)
                true
            } else false
        }

        override val size: Int get() = sparseBooleanArray.size()
        override fun contains(element: Int) = sparseBooleanArray.containsKey(element)
        override fun isEmpty() = sparseBooleanArray.isEmpty()
        override fun toString() = sparseBooleanArray.toString()
    }

    class EntryIterator(private val sparseBooleanArray: SparseBooleanArray) :
        MutableIterator<MutableMap.MutableEntry<Int, Boolean>> {
        private var point: Int = 0
        private var mEntryValid = false
        private var key: Int = 0
        override fun hasNext() = point + (if (mEntryValid) 1 else 0) < sparseBooleanArray.size()
        override fun next(): MutableMap.MutableEntry<Int, Boolean> {
            if (!hasNext()) throw NoSuchElementException()
            if (mEntryValid) point++
            mEntryValid = true
            val k = sparseBooleanArray.keyAt(point)
            key = k
            return InternalNode(k, sparseBooleanArray)
        }

        override fun remove() {
            check(mEntryValid)
            val index = sparseBooleanArray.indexOfKey(key)
            val valueExist = index >= 0
            check(valueExist)
            if (valueExist) IntBoolAddition.removeAt(sparseBooleanArray, index)
            mEntryValid = false
        }

        private class InternalNode(
            private val k: Int,
            private val sparseBooleanArray: SparseBooleanArray,
        ) : NodeA<Int, Boolean>() {
            override val key: Int
                get() = k
            override val value: Boolean
                get() = IntBoolAddition.get(sparseBooleanArray, key)!!

            override fun setValue(newValue: Boolean)=
                  IntBoolAddition.put(sparseBooleanArray, key, newValue)!!
        }
    }

    class EntrySet(private val sparseBooleanArray: SparseBooleanArray) :
        EntrySetI<Int, Boolean> {
        override fun add(element: MutableMap.MutableEntry<Int, Boolean>): Boolean {
            if (contains(element)) return false
            sparseBooleanArray.put(element.key, element.value)
            return true
        }

        override fun clear() = sparseBooleanArray.clear()

        override fun iterator() = EntryIterator(sparseBooleanArray)

        override fun remove(element: MutableMap.MutableEntry<Int, Boolean>) =
            sparseBooleanArray.remove(element.key, element.value)

        override val size: Int = sparseBooleanArray.size()
        override fun contains(element: MutableMap.MutableEntry<Int, Boolean>): Boolean {
            val index=sparseBooleanArray.indexOfKey(element.key)
            return if (index>=0)
                sparseBooleanArray.valueAt(index)==element.value
            else false
        }

        override fun isEmpty() = sparseBooleanArray.isEmpty()

        override fun toString() = sparseBooleanArray.toString()
    }
}

private object IntBoolAddition {

    fun removeAt(sparseBooleanArray: SparseBooleanArray, index: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) sparseBooleanArray.removeAt(index)
        else sparseBooleanArray.remove(sparseBooleanArray.keyAt(index),
            sparseBooleanArray.valueAt(index))
    }

    fun put(sparseBooleanArray: SparseBooleanArray, key: Int, value: Boolean): Boolean? {
        val old = get(sparseBooleanArray, key)
        sparseBooleanArray.put(key, value)
        return old
    }

    fun get(sparseBooleanArray: SparseBooleanArray, key: Int): Boolean? {
        val ret = sparseBooleanArray.get(key)
        if (!ret) if (!sparseBooleanArray.containsKey(key)) return null
        return ret
    }
}