package com.privacyFirst.usageStats.underconstruction

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.os.Build
import com.privacyFirst.usageStats.dymaticLib.slimOrFastestMap.MapDowngradeIntV
import com.privacyFirst.usageStats.staticLib.date.DateTrans
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.StreamSupport
import kotlin.reflect.full.staticProperties

object UsageEventClass {

    public val eventMap: Map<Int, String>
        get() {
            val c = UsageEvents.Event::class
            val sp=c.staticProperties
            val chm = ConcurrentHashMap<Int, String>(sp.count())
                sp.parallelStream()
                    .forEach { staticProperty ->
                    var deprecated = false
                    staticProperty.annotations
                        .filter { annotations->annotations.annotationClass.simpleName == "Deprecated" }
                        .forEach { deprecated = true }
                    val k=staticProperty.get()

                    if(k is Int)
                        if(deprecated){
                            chm.putIfAbsent(k,staticProperty.name)
                        }else{
                            chm[k] = staticProperty.name
                        }
                }
            return MapDowngradeIntV(chm)
        }

    fun a(usm: UsageStatsManager, b: Long, e: Long) {
        val qe = usm.queryEvents(b, e)
        val iterator = EventIterator(qe)

        val targetStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val iterable = Iterable { iterator }
            StreamSupport.stream(iterable.spliterator(), true)
        } else {
            val list = ArrayList<UsageEvents.Event>()
            iterator.forEach { i -> list.add(i) }
            list.stream()
        }
        targetStream.forEach { it ->

        }
    }

    fun eventToString(event: UsageEvents.Event): String {
        val sb = StringBuilder()
        sb.append("timeStamp: " + DateTrans.stamp(event.timeStamp) + "\n")
        sb.append("className: " + event.className + "\n")
        val eventType = event.eventType
        sb.append("eventType: " + eventMap[eventType] + "\n")
        sb.append("packageName: " + event.packageName + "\n")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            if (event.eventType == UsageEvents.Event.SHORTCUT_INVOCATION)
                sb.append("eventType: " + event.shortcutId + "\n")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            if (event.eventType == UsageEvents.Event.STANDBY_BUCKET_CHANGED)
                sb.append("eventType: " + event.appStandbyBucket + "\n")


        ConfigurationClass.configurationToString(event.configuration)
        return sb.toString()
    }

}

private class EventIterator(private val usageEvents: UsageEvents) :
    Iterator<UsageEvents.Event> {
    override fun hasNext() =
        usageEvents.hasNextEvent()

    override fun next(): UsageEvents.Event {
        val event = UsageEvents.Event()
        usageEvents.getNextEvent(event)
        return event
    }
}