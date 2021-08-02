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
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val itemsList = ArrayList<PackageValue>()
    private lateinit var customAdapter: CustomAdapter
    //private var startTime: Long = 0
    //private var endTime: Long = 0
    //private val onlyOneDialog = ReentrantLock()
    //private var onlyOneDialogAtom = false
    private val onlyOneDialog=AtomicBoolean()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "appUsageInfo"
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        customAdapter = CustomAdapter(itemsList)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = customAdapter

        val time = System.currentTimeMillis()
        val sTime = time - 7 * 24 * 60 * 60 * 1000
        val eTime = time
        prepareItems(sTime,eTime)
    }

    private fun prepareItems(sTime:Long,eTime:Long) {

        itemsList.clear()
        val mUsageStatsManager =
            getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val queryUsageStats =
            mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, sTime, eTime)
        for (i in queryUsageStats) {
            val appInfo: ApplicationInfo? =
                try { packageManager.getApplicationInfo(i.packageName, PackageManager.GET_META_DATA)
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            if (appInfo == null) {
                val value = PackageValue("-uninstalled- " + i.packageName, i)
                itemsList.add(value)
            } else {
                val appName = appInfo.loadLabel(packageManager).toString()
                val de = packageName(
                    appName,
                    i.packageName
                )
                val des1 = de.s1
                val value = if (des1 == "") PackageValue(i.packageName, i)
                else PackageValue("(" + de.s1RemoveDot + ") " + i.packageName, i)
                itemsList.add(value)
            }
        }
        customAdapter.notifyDataSetChanged()

    }


    fun onChangeClick(view: View) {
        val tmpV =onlyOneDialog.getAndSet(true)
        if (!tmpV) {
            MaterialAlertDialogBuilder(view.context)
                .setTitle(title)
                .setOnCancelListener {
                    onlyOneDialog.set(false)
                }
                .setOnDismissListener {
                    onlyOneDialog.set(false)
                }
                .setView(R.layout.date_text_input)
                .setPositiveButton("apply") { dialog, _ ->
                    val input = (dialog as AlertDialog).findViewById<TextView>(R.id.text_input)
                    val f = SimpleDateFormat("y-MM-dd HH:mm:ss::SSS Z")
                    val array = input!!.text.split("|")
                    if (array.size != 2) {
                        Toast.makeText(view.context, "need two time stamp", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        val date1: Date? = try {
                            f.parse(array[0])
                        } catch (e: ParseException) {
                            null
                        }
                        val date2: Date? = try {
                            f.parse(array[1])
                        } catch (e: ParseException) {
                            null
                        }

                        if (date1 == null) {
                            Toast.makeText(
                                view.context,
                                " time stamp 1 error. format:'1970-01-01 00:00:00:000 +0000'",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (date2 == null) {
                            Toast.makeText(
                                view.context,
                                " time stamp 2 error. format:'1970-01-01 00:00:00:000 +0000'",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (date1 != null && date2 != null) {
                            val t1 = date1.time
                            val t2 = date2.time
                            if (t1 <= t2) {
                                prepareItems(t1,t2)
                            } else {
                                Toast.makeText(
                                    view.context,
                                    "startTime should smaller than endTime'",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    onlyOneDialog.set(false)
                }
                .show()
        }


    }

    fun myItemClick(view: View) {
        val tmpV =onlyOneDialog.getAndSet(true)
        if (!tmpV) {
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            val position = recyclerView.getChildAdapterPosition(view)
            val sb1 = StringBuilder()
            val ua = itemsList[position].usageStats
            sb1.append("packageName:" + ua.packageName + "\n")
            sb1.append("firstTimeStamp:" + DateTrans.transDate(ua.firstTimeStamp) + "\n")
            sb1.append("lastTimeStamp:" + DateTrans.transDate(ua.lastTimeStamp) + "\n")
            sb1.append("lastTimeUsed:" + DateTrans.transDate(DateTrans.transDateFilter(ua.firstTimeStamp,ua.lastTimeUsed)) + "\n")
            sb1.append("totalTimeInForeground:" + DateTrans.transTime(ua.totalTimeInForeground) + "\n")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                sb1.append("totalTimeForegroundServiceUsed:" + DateTrans.transDate(DateTrans.transDateFilter(ua.firstTimeStamp,ua.totalTimeForegroundServiceUsed)) + "\n")
                sb1.append("lastTimeVisible:" + DateTrans.transDate(ua.lastTimeVisible) + "\n")
                sb1.append("lastTimeForegroundServiceUsed:" + DateTrans.transDate(DateTrans.transDateFilter(ua.firstTimeStamp,ua.lastTimeForegroundServiceUsed)) + "\n")
                sb1.append("totalTimeVisible:" + DateTrans.transTime(ua.totalTimeVisible) + "\n")
            }
            MaterialAlertDialogBuilder(view.context)
                .setTitle(ua.packageName)
                .setMessage(sb1.toString())
                .setNegativeButton("Cancel") { _, _ ->
                    onlyOneDialog.set(false)
                }
                .setOnCancelListener {
                    onlyOneDialog.set(false)
                }
                .setOnDismissListener {
                    onlyOneDialog.set(false)
                }
                .show()
        }
    }
}

private class PackageValue constructor(val info: String, val usageStats: UsageStats)

private class CustomAdapter(private var itemsList: ArrayList<PackageValue>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTextView: TextView = view.findViewById(R.id.itemTextView)
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
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}