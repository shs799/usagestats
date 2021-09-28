package com.privacyFirst.usageStats.desugar

import android.os.Build
import androidx.annotation.RequiresApi

@Deprecated("Check Desugar, Do not use it.",level = DeprecationLevel.HIDDEN)
private object Check {
    @RequiresApi(Build.VERSION_CODES.P)
    fun typeName() {
        val al = ArrayList<Any>()
        al.javaClass.declaredFields[0].genericType.typeName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun withInitial() {
        ThreadLocal.withInitial { }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun spliterator() {
        val list = ArrayList<String>()
        val iterator = list.iterator()
        val iterable = Iterable { iterator }
        iterable.spliterator()
    }
}