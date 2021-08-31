package com.privacyFirst.usageStats

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.privacyFirst.usageStats.CustomAdapter.OnItemClickListener
import com.privacyFirst.usageStats.staticlib.date.DateToday
import com.privacyFirst.usageStats.staticlib.date.DateTrans
import com.privacyFirst.usageStats.staticlib.string.StripString
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

import com.privacyFirst.usageStats.dymaticlib.mapDowngrade.MapDowngradeIntV


class MainActivity : AppCompatActivity() {
    private var itemsListMap  = MapDowngradeIntV< PackageValue>()

    private val onlyOneDialog = AtomicBoolean()

    private var clickeditem: Int = 0

    private val dateTrans= DateTrans

    //order:1->maxwidth, 2->customAdapter
    //private val maxwidth = AtomicInteger()//not atomic operation just save a value
    private val customAdapter = CustomAdapter(itemsListMap)

    //order:1->todayInUtcMilliseconds, 2->choices
    private var todayInUtcMilliseconds: Long = DateToday.todayInUtcMilliseconds()
    private val choices: Array<String> = Array(10) { i ->
        forEachChoice(i)
    }


    private fun forEachChoice(i: Int): String {
        /*10 is mean
        *today|today-1|...|today-6|week_range|month_range|year_range
        */
        val ap = "(apply_to_show)"
        when (i) {
            7 -> return "week_range$ap"
            8 -> return "month_range$ap"
            9 -> return "year_range$ap"
        }
        return dateTrans.dateStamp(todayInUtcMilliseconds - i * 24 * 60 * 60 * 1000)
    }

    private fun flushChoice() {
        if (DateToday.todayInUtcMilliseconds() != todayInUtcMilliseconds) {
            todayInUtcMilliseconds = DateToday.todayInUtcMilliseconds()
            for (i in 0..6)
                choices[i] = forEachChoice(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "AppUsage"
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager

        customAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                myItemClick(view, position)
            }
        })

        recyclerView.adapter = customAdapter

        val button = findViewById<FloatingActionButton>(R.id.floating_action_button)
        button.setOnClickListener { view ->
            onChangeClick(view)
        }

        val sTime = todayInUtcMilliseconds - 24 * 60 * 60 * 1000
        val eTime = todayInUtcMilliseconds
        refreshItem(
            UsageStatsManager.INTERVAL_DAILY, sTime, eTime,
            findViewById<LinearLayout>(android.R.id.content)
        )
    }

    private fun refreshItem(intervalType: Int, sTime: Long, eTime: Long, snakeBarView: View) {

        val mUsageStatsManager =
            getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val queryUsageStats =
            mUsageStatsManager.queryUsageStats(intervalType, sTime, eTime)
        if (queryUsageStats.size != 0) {
            /**choice[7]->weak
             *choice[8]->month
             * choice[9]->year
             */
            if (intervalType in 1..3)
                choices[6 + intervalType] =
                    "From " + dateTrans.stamp(queryUsageStats[0].firstTimeStamp)
        } else {
            Snackbar.make(snakeBarView, "No data", Snackbar.LENGTH_LONG)
                .setAction("Set permission") {
                    val int = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivity(int)
                }
                .show()
        }
        val a = AtomicInteger()
        val writeMap = ConcurrentHashMap<Int, PackageValue>()
        queryUsageStats.parallelStream().filter { i ->
            !(i.lastTimeUsed / (24 * 60 * 60 * 1000) == 0L && i.totalTimeInForeground == 0L)
        }.forEach { usageStats ->
            val appInfo = try {
                packageManager.getApplicationInfo(
                    usageStats.packageName, PackageManager.GET_META_DATA
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            if (appInfo == null) {
                val value = PackageValue("-uninstalled-", usageStats)
                writeMap[a.getAndIncrement()] = value
            } else {
                val appName = appInfo.loadLabel(packageManager).toString()
                val i = appInfo.loadIcon(packageManager)

                /*if (appInfo.packageName == packageName) {
                    maxwidth.set(i.minimumWidth)
                }*/

                //set.add(i.minimumWidth)
                val st = StripString.strip(Pair(appName, usageStats.packageName))
                val value = if (st.first == "") PackageValue(usageStats.packageName, usageStats, i)
                else PackageValue(StripString.stripDot(st.first), usageStats, i)
                writeMap[a.getAndIncrement()] = value
            }
        }
        //appIconWidth = set.parallelStream().mapToInt { i -> i }.min().asInt
        //val map = TransData.listToMap(writeList)
        itemsListMap.resetMap(writeMap)
        customAdapter.notifyDataSetChanged()
    }


    private fun onChangeClick(view: View) {
        val tmpV = onlyOneDialog.getAndSet(true)
        if (!tmpV) {
            MaterialAlertDialogBuilder(view.context)
                .setTitle("Set range")
                .setSingleChoiceItems(choices, clickeditem, null)
                .setOnCancelListener {
                    onlyOneDialog.set(false)
                }
                .setOnDismissListener {
                    onlyOneDialog.set(false)
                }
                .setPositiveButton("apply&flush") { dialog, _ ->
                    val checkedItemPosition = (dialog as AlertDialog).listView.checkedItemPosition
                    clickeditem = checkedItemPosition
                    if (checkedItemPosition != AdapterView.INVALID_POSITION) {
                        flushChoice()
                        if (checkedItemPosition in 0..6) {
                            val sTime =
                                todayInUtcMilliseconds - (checkedItemPosition) * 24 * 60 * 60 * 1000+1
                            val eTime =
                                todayInUtcMilliseconds - (checkedItemPosition - 1) * 24 * 60 * 60 * 1000 - 2
                            refreshItem(UsageStatsManager.INTERVAL_DAILY, sTime, eTime, view)
                        } else {
                            val sTime = todayInUtcMilliseconds - 24 * 60 * 60 * 1000
                            val eTime = todayInUtcMilliseconds - 1
                            /**checkedItemPosition[7]->INTERVAL_WEEKLY
                             *checkedItemPosition[8]->INTERVAL_MONTHLY
                             * checkedItemPosition[9]->INTERVAL_YEARLY
                             */
                            if (checkedItemPosition in 7..9) {
                                refreshItem(
                                    -6 + checkedItemPosition, sTime, eTime, view
                                )
                            }
                        }
                    }
                    onlyOneDialog.set(false)
                }
                .setNegativeButton("cancel") { _, _ ->
                    onlyOneDialog.set(false)
                }
                .show()
        }
    }


    private fun myItemClick(view: View, position: Int) {
        val tmpV = onlyOneDialog.getAndSet(true)
        if (!tmpV) {
            val t = itemsListMap[position]!!
            val sb1 = StringBuilder(128)
            val ua = t.usageStats
            sb1.append("packageName:" + ua.packageName + "\n")
            sb1.append("firstTimeStamp:" + dateTrans.stamp(ua.firstTimeStamp) + "\n")
            sb1.append("lastTimeStamp:" + dateTrans.stamp(ua.lastTimeStamp) + "\n")
            sb1.append(
                "lastTimeUsed:" + dateTrans.dateFilter(
                    ua.lastTimeUsed
                ) + "\n"
            )
            sb1.append("totalTimeInForeground:" + dateTrans.time(ua.totalTimeInForeground) + "\n")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                sb1.append(
                    "totalTimeForegroundServiceUsed:" + dateTrans.dateFilter(
                        ua.totalTimeForegroundServiceUsed
                    ) + "\n"
                )
                sb1.append("lastTimeVisible:" + (dateTrans.time(ua.lastTimeVisible)) + "\n")
                sb1.append(
                    "lastTimeForegroundServiceUsed:" + dateTrans.dateFilter(
                        ua.lastTimeForegroundServiceUsed
                    ) + "\n"
                )
                sb1.append("totalTimeVisible:" + dateTrans.time(ua.totalTimeVisible) + "\n")
            }

            val blockListFieldsName = listOf(
                "mPackageName",

                "mPackageToken",

                "mBeginTimeStamp",
                "mEndTimeStamp",
                "mLastTimeUsed",
                "mTotalTimeInForeground",

                "mTotalTimeForegroundServiceUsed",
                "mLastTimeVisible",
                "mLastTimeForegroundServiceUsed",
                "mTotalTimeVisible",

                "CREATOR"
            )
            val nullValue = listOf("{}", "0")

            val cm = ConcurrentSkipListMap<String, String>()//key value
            ua.javaClass.declaredFields.toList().parallelStream().filter { i ->
                !blockListFieldsName.contains(i.name)
            }
                .forEach { i ->
                    i.isAccessible = true
                    val get = i.get(ua)
                    val v =
                        if (i.name.contains("TimeUsed") && i.genericType.typeName == "long") {
                            if (get != null) dateTrans.dateFilter(
                                get.toString().toLong()
                            ) else "null"
                        } else if (i.name.contains("Time") && i.genericType.typeName == "long") {
                            if (get != null) dateTrans.time(get.toString().toLong()) else "null"
                        } else {
                            get?.toString() ?: "null"
                        }
                    if (!nullValue.contains(v))
                        cm[i.name] = v

                    i.isAccessible = false
                }

            cm.forEach { (k, v) ->
                sb1.append("#$k:$v\n")
            }
            MaterialAlertDialogBuilder(view.context)
                .setTitle(t.appLabel)
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

private class PackageValue {
    val appLabel: String
    val usageStats: UsageStats
    val appIcon: Drawable?

    constructor(appLabel: String, usageStats: UsageStats) {
        this.appLabel = appLabel
        this.usageStats = usageStats
        this.appIcon = null
    }

    constructor(appLabel: String, usageStats: UsageStats, appIcon: Drawable) {
        this.appLabel = appLabel
        this.usageStats = usageStats
        this.appIcon = appIcon
    }
}

private class CustomAdapter(
    private val itemsList: MapDowngradeIntV<PackageValue>,
    //private val maxWidth: AtomicInteger,
) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTextView: TextView = view.findViewById(R.id.itemTextView)
        val itemTextView2: TextView = view.findViewById(R.id.itemTextView2)
        val appIcon: ImageView = view.findViewById(R.id.appicon)
        val cardView: MaterialCardView = view.findViewById(R.id.card)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener?) {
        if (mOnItemClickListener != null) {
            this.mOnItemClickListener = mOnItemClickListener
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val custom = itemsList[position]!!
        holder.cardView.setOnClickListener { view ->
            mOnItemClickListener?.onItemClick(
                view,
                position
            )
        }
        holder.itemTextView.text = custom.appLabel
        val p = StripString.strip(Pair(custom.appLabel, custom.usageStats.packageName))
        holder.itemTextView2.text = StripString.stripDot(p.second)
        holder.appIcon.setImageDrawable(custom.appIcon)

        /*if(maxWidth.get()!=0){
            holder.appIcon.maxWidth=maxWidth.get()
        }*/
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}