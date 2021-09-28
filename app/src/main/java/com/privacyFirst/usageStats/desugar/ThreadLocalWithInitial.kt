package com.privacyFirst.usageStats.desugar

import java.util.function.Supplier

class ThreadLocalWithInitial<T>(private val supplier: Supplier<out T?>): ThreadLocal<T>() {
    override fun initialValue() = supplier.get()
}