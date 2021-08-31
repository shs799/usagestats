package com.privacyFirst.usageStats

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import com.privacyFirst.usageStats.staticMethod.DateToday
import java.util.concurrent.atomic.AtomicReference

class Background {
    private val usageStatsManager: UsageStatsManager
    private var list : MutableList<AtomicReference<List<UsageStats>>> = MutableList(10){
        AtomicReference<List<UsageStats>>()
    }
    private val todayInUtcMilliseconds: Long
    //private val cachePool= Executors.newCachedThreadPool()

    /*private val thread:MutableList<Future<List<UsageStats>>?> = MutableList(10){
        null
    }*/

    constructor(usageStatsManager: UsageStatsManager) {

        this.usageStatsManager = usageStatsManager
        this.todayInUtcMilliseconds = DateToday.todayInUtcMilliseconds()
    }



    private fun initEach(i: Int): List<UsageStats> {
        if(i<0||i>9){
            throw Exception("error number")
        }
        val sTime = todayInUtcMilliseconds - 24 * 60 * 60 * 1000
        val eTime = todayInUtcMilliseconds - 1
        if (i in 7..9) {
            return usageStatsManager.queryUsageStats(-6 + i, sTime, eTime)
        } else {
            return usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                todayInUtcMilliseconds - (i) * 24 * 60 * 60 * 1000,
                todayInUtcMilliseconds - (i - 1) * 24 * 60 * 60 * 1000 - 1
            )
        }
    }
    /*private fun initAll():List<List<UsageStats>> {
        val mutableList:MutableList<List<UsageStats>?> = MutableList(10){null}
        /*val l=List(10) { i->
            Runnable {

            //todo:write to storage
            }
        }*/
        val l2= List(10){i->
            val t=Thread {
                mutableList[i]=initEach(i)
            }
            t.run()
            t
        }
        /*val result= List(10){i->
            l2[i].get()
        }*/
        return result
    }
    fun init(){
        cachePool.submit{
            val l=initAll()
            list.set(l)
        }

    }*/
}