package com.privacyFirst.usageStats.staticLib.concurrent

import java.util.concurrent.atomic.AtomicReference
import java.util.function.BinaryOperator
import java.util.function.UnaryOperator

class AtomicReferenceKT<V> {
    private val ar: AtomicReference<V>

    constructor() {
        ar = AtomicReference()
    }

    constructor(initialValue: V) {
        ar = AtomicReference(initialValue)
    }

    fun set(newValue: V) = ar.set(newValue)
    fun lazySet(newValue: V) = ar.lazySet(newValue)
    fun compareAndSet(expect: V, update: V) = ar.compareAndSet(expect, update)
    fun weakCompareAndSet(expect: V, update: V) = ar.weakCompareAndSet(expect, update)
    fun get(): V? = ar.get()
    fun getAndSet(newValue: V): V? = ar.getAndSet(newValue)
    fun getAndUpdate(updateFunction: UnaryOperator<V>): V? = ar.getAndUpdate(updateFunction)
    fun updateAndGet(updateFunction: UnaryOperator<V>): V? = ar.updateAndGet(updateFunction)
    fun getAndAccumulate(update: V, accumulatorFunction: BinaryOperator<V>): V? =
        ar.getAndAccumulate(update, accumulatorFunction)

    fun accumulateAndGet(update: V, accumulatorFunction: BinaryOperator<V>): V? =
        ar.accumulateAndGet(update, accumulatorFunction)

    override fun toString() = ar.toString()
}