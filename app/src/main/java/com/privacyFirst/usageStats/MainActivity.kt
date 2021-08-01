package com.privacyFirst.usageStats

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val itemsList = ArrayList<PackageValue>()
    private lateinit var customAdapter: CustomAdapter
    private var startTime: Long = 0
    private var endTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "appUsageInfo"
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter
        prepareItems()

    }

    private fun prepareItems() {
        val time = System.currentTimeMillis()

        startTime = time - 7 * 24 * 60 * 60 * 1000
        endTime = time

        val mUsageStatsManager =
            getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val queryUsageStats =
            mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime)
        for (i in queryUsageStats) {
            //var appName: String? = null
            val appInfo: ApplicationInfo? =
            try {
                  packageManager.getApplicationInfo(
                    i.packageName,
                    PackageManager.GET_META_DATA
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            if (appInfo == null) {
                val value = PackageValue("-uninstalled- " + i.packageName, i)
                itemsList.add(value)
            } else {
                val appName = appInfo.loadLabel(packageManager).toString()
                val de = dedupString(appName, i.packageName)
                val des1 = de.s1
                val value = if (des1 == "") PackageValue(i.packageName, i)
                else PackageValue("(" + de.s1 + ") " + i.packageName, i)
                itemsList.add(value)
            }
        }
        customAdapter.notifyDataSetChanged()
        
    }

    fun onChangeClick(view:View){
        MaterialAlertDialogBuilder(view.context)
            .setTitle(title)
            .setView(R.layout.date_text_input)
            .setPositiveButton("apply") { dialog, _ ->
                val input = (dialog as AlertDialog).findViewById<TextView>(android.R.id.text1)
                val f=SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z")
                //input!!.text=f.format(Date(startTime))+"|"+f.format(Date(endTime))
                val array=input!!.text.split("|")
                if(array.size!=2) {
                    Toast.makeText(view.context, "need two time stamp", Toast.LENGTH_LONG).show()
                }else{
                    val date1:Date?=try{
                        f.parse(array[0])
                    }catch (e: ParseException){
                        null
                    }
                    val date2:Date?=try{
                        f.parse(array[1])
                    }catch (e: ParseException){
                        null
                    }

                    if(date1==null){
                        Toast.makeText(view.context, " time stamp 1 error. format:'1970-01-01 00:00:00:000 +0000'", Toast.LENGTH_LONG).show()
                    }
                    if(date2==null){
                        Toast.makeText(view.context, " time stamp 2 error. format:'1970-01-01 00:00:00:000 +0000'", Toast.LENGTH_LONG).show()
                    }
                    if(date1!=null&&date2!=null){
                        val t1=date1.time
                        val t2=date2.time
                        if(t1<=t2){
                            startTime=t1
                            endTime=t2
                        }else{
                            Toast.makeText(view.context, "startTime should smaller than endtime'", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .show()



    }

    fun myItemClick(view: View) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val position = recyclerView.getChildAdapterPosition(view)
        val sb1 = StringBuilder()
        val ua = itemsList[position].usageStats
        sb1.append("packageName:" + ua.packageName +"\n")
        sb1.append("firstTimeStamp:" + DateTrans().transDateFilter(ua.firstTimeStamp) +"\n")
        sb1.append("lastTimeStamp:" + DateTrans().transDateFilter(ua.lastTimeStamp) +"\n")
        sb1.append("lastTimeUsed:" + DateTrans().transDateFilter(ua.lastTimeUsed) +"\n")
        sb1.append("totalTimeInForeground:" + DateTrans().transTime(ua.totalTimeInForeground) +"\n")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sb1.append("totalTimeForegroundServiceUsed:" + DateTrans().transTime(ua.totalTimeForegroundServiceUsed) +"\n")
            sb1.append("lastTimeVisible:" + DateTrans().transDateFilter(ua.lastTimeVisible) +"\n")
            sb1.append("lastTimeForegroundServiceUsed:" + DateTrans().transDateFilter(ua.lastTimeForegroundServiceUsed) +"\n")
            sb1.append("totalTimeVisible:" + DateTrans().transTime(ua.totalTimeVisible) +"\n")
        }
        MaterialAlertDialogBuilder(view.context)
            .setTitle(ua.packageName)
            .setMessage(sb1.toString())
            .setNegativeButton("close", null)
            .show()
    }
}

private class PackageValue constructor(val info: String, val usageStats: UsageStats)

private class CustomAdapter(private var itemsList: ArrayList<PackageValue>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTextView: TextView = view.findViewById(R.id.itemTextView)
        //val cardView: MaterialCardView = view.findViewById(R.id.MDCardView)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position].info
        holder.itemTextView.text = item
        //holder.cardView.setOnClickListener {

        //}
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}