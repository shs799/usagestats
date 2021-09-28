package com.privacyFirst.usageStats.underconstruction

import java.util.concurrent.atomic.AtomicInteger

class AtomicIntegerFunction(private var ai: AtomicInteger) {
    private var judge: Judge? = null

    fun judge(judge: Judge): AtomicIntegerFunction {
        this.judge = judge
        return this
    }

    private var newValue: NewValue? = null

    fun newValue(newValue: NewValue): AtomicIntegerFunction {
        this.newValue = newValue
        return this
    }

    private var solve: Function? = null

    fun solve(solve: Function): AtomicIntegerFunction {
        this.solve = solve
        return this
    }

    fun execute() {
        while (true) {
            val v = ai.get()
            if (judge?.compare(v) != true) {
                val nv = newValue?.get(v) ?: v
                if (ai.compareAndSet(v, nv)) {
                    solve?.run(v, nv)
                }
            }
        }
    }
}

interface Judge {
    fun compare(v: Int): Boolean {
        return true
    }
}

interface NewValue {
    fun get(oldValue: Int): Int {
        return oldValue
    }
}

interface Function {
    fun run(oldValue: Int, newValue: Int) {

    }
}