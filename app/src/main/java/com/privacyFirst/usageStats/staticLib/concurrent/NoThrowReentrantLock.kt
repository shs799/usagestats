package com.privacyFirst.usageStats.staticLib.concurrent

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

class NoThrowReentrantLock {

    private val count = AtomicInteger()
    private val lock = ReentrantLock()

    fun lock() {
        lock.lock()
        count.getAndIncrement()
    }

    fun unlock() {
        while (true) {
            val c = count.get()
            if (c > 0) {
                if (count.compareAndSet(c, c - 1)) {
                    lock.unlock()
                    return
                }
            } else return
        }
    }

    fun unlockAll() =
        repeat(count.getAndSet(0)) { lock.unlock() }

}