package com.privacyFirst.usageStats

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import com.privacyFirst.usageStats.staticMethod.DateToday
import com.privacyFirst.usageStats.staticMethod.DateTrans
import com.privacyFirst.usageStats.staticMethod.StripString
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private val itemsList = ArrayList<PackageValue>()
    private val customAdapter: CustomAdapter = CustomAdapter(itemsList)
    private val onlyOneDialog = AtomicBoolean()

    //order:todayInUtcMilliseconds choices
    private var todayInUtcMilliseconds: Long = DateToday.todayInUtcMilliseconds()
    private val choices: Array<String> = Array(10) { i ->
        forEachChoice(i)
    }
    private var appIconWidth: Int = 0

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
        return DateTrans.dateStamp(todayInUtcMilliseconds - i * 24 * 60 * 60 * 1000)
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
        title = "UsageStats"
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager

        customAdapter.run {
            recyclerView.layoutManager = layoutManager

            setOnItemClickListener(object : OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    myItemClick(view, position)
                }
            })
        }
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
        itemsList.clear()
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
                    "From " + DateTrans.stamp(queryUsageStats[0].firstTimeStamp)
        } else {
            Snackbar.make(snakeBarView, "No data", Snackbar.LENGTH_LONG)
                .setAction("Set permission") {
                    val int = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivity(int)
                }
                .show()
        }
        val writeList = ConcurrentLinkedQueue<PackageValue>()
        val set = ConcurrentSkipListSet<Int>()
        queryUsageStats.parallelStream().forEach { usageStats ->
            val appInfo = try {
                packageManager.getApplicationInfo(
                    usageStats.packageName, PackageManager.GET_META_DATA
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            if (appInfo == null) {
                val value = PackageValue("-uninstalled-", usageStats)
                writeList.add(value)
            } else {
                val appName = appInfo.loadLabel(packageManager).toString()
                val i = appInfo.loadIcon(packageManager)
                set.add(i.minimumWidth)
                val st = StripString.strip(Pair(appName, usageStats.packageName))
                val value = if (st.first == "") PackageValue(usageStats.packageName, usageStats, i)
                else PackageValue(StripString.stripDot(st.first), usageStats, i)
                writeList.add(value)
            }
        }
        appIconWidth = set.parallelStream().mapToInt { i -> i }.min().asInt
        itemsList.addAll(writeList)
        customAdapter.notifyDataSetChanged()
    }

    private fun onChangeClick(view: View) {
        val tmpV = onlyOneDialog.getAndSet(true)
        if (!tmpV) {
            MaterialAlertDialogBuilder(view.context)
                .setTitle("Set range")
                .setSingleChoiceItems(choices, 1, null)
                .setOnCancelListener {
                    onlyOneDialog.set(false)
                }
                .setOnDismissListener {
                    onlyOneDialog.set(false)
                }
                .setPositiveButton("apply&flush") { dialog, _ ->
                    val checkedItemPosition = (dialog as AlertDialog).listView.checkedItemPosition
                    if (checkedItemPosition != AdapterView.INVALID_POSITION) {
                        flushChoice()
                        if (checkedItemPosition in 0..6) {
                            val sTime =
                                todayInUtcMilliseconds - (checkedItemPosition) * 24 * 60 * 60 * 1000
                            val eTime =
                                todayInUtcMilliseconds - (checkedItemPosition - 1) * 24 * 60 * 60 * 1000 - 1
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
            val sb1 = StringBuilder(1 shl 9)//512
            val ua = itemsList[position].usageStats
            sb1.append("packageName:" + ua.packageName + "\n")
            sb1.append("firstTimeStamp:" + DateTrans.stamp(ua.firstTimeStamp) + "\n")
            sb1.append("lastTimeStamp:" + DateTrans.stamp(ua.lastTimeStamp) + "\n")
            sb1.append(
                "lastTimeUsed:" + DateTrans.dateFilter(
                    ua.lastTimeUsed
                ) + "\n"
            )
            sb1.append("totalTimeInForeground:" + DateTrans.time(ua.totalTimeInForeground) + "\n")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                sb1.append(
                    "totalTimeForegroundServiceUsed:" + DateTrans.dateFilter(
                        ua.totalTimeForegroundServiceUsed
                    ) + "\n"
                )
                sb1.append("lastTimeVisible:" + (DateTrans.time(ua.lastTimeVisible)) + "\n")
                sb1.append(
                    "lastTimeForegroundServiceUsed:" + DateTrans.dateFilter(
                        ua.lastTimeForegroundServiceUsed
                    ) + "\n"
                )
                sb1.append("totalTimeVisible:" + DateTrans.time(ua.totalTimeVisible) + "\n")
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
            ua.javaClass.declaredFields.toList().parallelStream()
                .forEach(fun(i: Field) {
                    i.isAccessible = true
                    val get = i.get(ua)
                    if (!blockListFieldsName.contains(i.name)) {
                        //typeName:API limit about typeName is a bug, we should not care about it.
                        val v =
                            if (i.name.contains("TimeUsed") && i.genericType.typeName == "long") {
                                if (get != null) DateTrans.dateFilter(
                                    get.toString().toLong()
                                ) else "null"
                            } else if (i.name.contains("Time") && i.genericType.typeName == "long") {
                                if (get != null) DateTrans.time(get.toString().toLong()) else "null"
                            } else {
                                get?.toString() ?: "null"
                            }
                        if (!nullValue.contains(v))
                            cm[i.name] = v
                    }
                    i.isAccessible = false
                })

            cm.forEach { (k, v) ->
                sb1.append("#$k:$v\n")
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

private class CustomAdapter(private var itemsList: ArrayList<PackageValue>) :
    RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mappIconWidth: Int = 0
        get() = field
        set(value) {
            field = value
        }

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
        val custom = itemsList[position]
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
        //holder.appIcon.maxWidth = mappIconWidth//toDo:change
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}