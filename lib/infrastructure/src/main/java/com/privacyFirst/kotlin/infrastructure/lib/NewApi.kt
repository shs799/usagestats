package com.privacyFirst.kotlin.infrastructure.lib

import android.os.Build
import android.util.SparseLongArray
import androidx.annotation.RequiresApi

@Deprecated("Check NewApi, Do not use it.",level = DeprecationLevel.HIDDEN)
private object NewApi {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun setValueAt(){
        val s=SparseLongArray()
        //s.setValueAt()
    }
}