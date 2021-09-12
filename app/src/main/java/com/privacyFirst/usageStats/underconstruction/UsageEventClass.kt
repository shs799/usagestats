package com.privacyFirst.usageStats.underconstruction

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.os.Build
import com.privacyFirst.usageStats.staticlib.date.DateTrans
import java.util.stream.StreamSupport

class UsageEventClass {

    fun a(usm: UsageStatsManager, b: Long, e: Long) {
        val qe = usm.queryEvents(b, e)
        val iterator= Iterator(qe)
        val iterable = Iterable { iterator }
        val targetStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            StreamSupport.stream(iterable.spliterator(), true)
         else {
            val list=ArrayList<UsageEvents.Event>()
            iterator.forEach { i-> list.add(i) }
            list.parallelStream()
        }
        targetStream.forEach { it->

        }
    }
    fun aEvent(event:UsageEvents.Event){
        DateTrans.stamp(event.timeStamp)
        event.className
        val conf=event.configuration
        conf.fontScale
        conf.mcc
        conf.mnc
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            conf.locales.toLanguageTags()
        }else{
            conf.locale.toLanguageTag()
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

        event.eventType
        event.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            event.shortcutId
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            conf.colorMode
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            event.appStandbyBucket
        }
    }


}

private class Iterator(private val usageEvents: UsageEvents) :
    kotlin.collections.Iterator<UsageEvents.Event> {
    override fun hasNext()=
        usageEvents.hasNextEvent()

    override fun next(): UsageEvents.Event {
        val event=UsageEvents.Event()
        usageEvents.getNextEvent(event)
        return event
    }
}