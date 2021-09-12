package com.privacyFirst.usageStats.underconstruction

import android.app.usage.UsageStatsManager
import android.os.Build

class configuration {
    fun a(usm:UsageStatsManager,i:Int,s:Long,e:Long){
        val qc=usm.queryConfigurations(i,s,e)
        for(i in qc){
            i.firstTimeStamp
            i.lastTimeStamp
            i.lastTimeActive
            i.totalTimeActive
            i.activationCount

            val conf=i.configuration
            conf.fontScale
            conf.mcc
            conf.mnc
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                conf.locales
            }else{
                conf.locale
            }
            conf.touchscreen
            conf.keyboard
            conf.keyboardHidden
            conf.hardKeyboardHidden
            conf.navigation
            conf.navigationHidden
            conf.orientation
            conf.screenLayout
            conf.uiMode
            conf.screenWidthDp
            conf.screenHeightDp
            conf.smallestScreenWidthDp
            conf.densityDpi
        }

    }
}