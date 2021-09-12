package com.privacyFirst.usageStats

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.privacyFirst.usageStats.CustomAdapter.OnItemClickListener
import com.privacyFirst.usageStats.Permission.checkUsageStatsPermission
import com.privacyFirst.usageStats.dymaticlib.mapDowngrade.MapDowngradeIntV
import com.privacyFirst.usageStats.staticlib.date.DateTrans
import com.privacyFirst.usageStats.staticlib.string.StripString
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class MainActivity : AppCompatActivity() {
    private var itemsListMap = MapDowngradeIntV<PackageValue>()

    private val onlyOneDialog = AtomicBoolean()

    private var clickedItem: Int = 0


    private val dateTrans =
        DateTrans


    //order:1->maxwidth, 2->customAdapter
    //private val maxwidth = AtomicInteger()//not atomic operation just save a value
    private val customAdapter = CustomAdapter(itemsListMap)

    //order:1->todayInUtcMilliseconds, 2->choices
    private var todayInUtcMilliseconds: Long = MaterialDatePicker.todayInUtcMilliseconds()
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
        val mTodayInUtcMilliseconds = MaterialDatePicker.todayInUtcMilliseconds()
        if (mTodayInUtcMilliseconds != todayInUtcMilliseconds) {
            todayInUtcMilliseconds = mTodayInUtcMilliseconds
            for (i in 0..6)
                choices[i] = forEachChoice(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", (Runtime.getRuntime().availableProcessors() +1).toString());
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)

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
    }

    override fun onResume() {
        super.onResume()
        if(itemsListMap.isEmpty()) {
            val sTime = todayInUtcMilliseconds - 24 * 60 * 60 * 1000
            val eTime = todayInUtcMilliseconds
            refreshItem(
                UsageStatsManager.INTERVAL_DAILY, sTime, eTime,
                findViewById<LinearLayout>(android.R.id.content)
            )
        }
    }

    private fun refreshItem(intervalType: Int, sTime: Long, eTime: Long, view: View) = runBlocking{
        async {
            if (!checkUsageStatsPermission(view.context))
                Snackbar
                    .make(view, "Need Permission", Snackbar.LENGTH_LONG)
                    .setAction("Set") {
                        val notice = "Select " + R.string.app_name + " ,then grant the permission."
                        Toast.makeText(it.context, notice, Toast.LENGTH_LONG).show()
                        val int = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        startActivity(int)
                    }
                    .show()
        }

        val mUsageStatsManager =
            getSystemService<UsageStatsManager>()
        val queryUsageStats =
            mUsageStatsManager?.queryUsageStats(intervalType, sTime, eTime) ?: emptyList()
        if (queryUsageStats.isNotEmpty()) {
            when (intervalType) {
                UsageStatsManager.INTERVAL_WEEKLY -> {
                    choices[7] =
                        "From " + dateTrans.stamp(queryUsageStats[0].firstTimeStamp)
                }
                UsageStatsManager.INTERVAL_MONTHLY -> {
                    choices[8] =
                        "From " + dateTrans.stamp(queryUsageStats[0].firstTimeStamp)
                }
                UsageStatsManager.INTERVAL_YEARLY -> {
                    choices[9] =
                        "From " + dateTrans.stamp(queryUsageStats[0].firstTimeStamp)
                }
            }
        } else {
            Snackbar
                .make(view, "No Data", Snackbar.LENGTH_SHORT)
                .show()
        }
        val a = AtomicInteger()
        val writeMap = ConcurrentHashMap<Int, PackageValue>()
        queryUsageStats.parallelStream().filter { i ->
            !(i.lastTimeUsed / (24 * 60 * 60 * 1000) == 0L && i.totalTimeInForeground == 0L)
        }.forEach { usageStats ->
            runBlocking{
            val appInfoF = async{try {
                packageManager.getApplicationInfo(
                    usageStats.packageName, PackageManager.GET_META_DATA
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }}
            val packageInfoF = async{try {
                packageManager.getPackageInfo(
                    usageStats.packageName, PackageManager.GET_META_DATA
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }}
                val appInfo=appInfoF.await()
                val packageInfo=packageInfoF.await()
            if (appInfo == null || packageInfo == null) {
                val value = PackageValue("-uninstalled-", usageStats, 0, 0)
                writeMap[a.getAndIncrement()] = value
            } else {
                val appName = appInfo.loadLabel(packageManager).toString()
                val icon = appInfo.loadIcon(packageManager)

                /*if (appInfo.packageName == packageName) {
                    maxwidth.set(i.minimumWidth)
                }*/

                //set.add(i.minimumWidth)
                val st = StripString.packageNameStrip(Pair(appName, usageStats.packageName))
                val value = if (st.first == "") PackageValue(usageStats.packageName,
                    usageStats,
                    icon,
                    packageInfo.firstInstallTime,
                    packageInfo.lastUpdateTime)
                else PackageValue(StripString.stripDot(st.first),
                    usageStats,
                    icon,
                    packageInfo.firstInstallTime,
                    packageInfo.lastUpdateTime)
                writeMap[a.getAndIncrement()] = value
            }
        }}
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
                .setSingleChoiceItems(choices, clickedItem, null)
                .setOnCancelListener {
                    onlyOneDialog.set(false)
                }
                .setOnDismissListener {
                    onlyOneDialog.set(false)
                }
                .setPositiveButton("apply&flush") { dialog, _ ->
                    val checkedItemPosition = (dialog as AlertDialog).listView.checkedItemPosition
                    clickedItem = checkedItemPosition
                    if (checkedItemPosition != AdapterView.INVALID_POSITION) {
                        flushChoice()
                        if (checkedItemPosition in 0..6) {
                            val sTime =
                                todayInUtcMilliseconds - (checkedItemPosition) * 24 * 60 * 60 * 1000 + 1
                            val eTime =
                                todayInUtcMilliseconds - (checkedItemPosition - 1) * 24 * 60 * 60 * 1000 - 2
                            refreshItem(UsageStatsManager.INTERVAL_DAILY, sTime, eTime, view)
                        } else {
                            val sTime = todayInUtcMilliseconds - 24 * 60 * 60 * 1000
                            val eTime = todayInUtcMilliseconds - 1
                            if (checkedItemPosition in 7..9) {
                                when (checkedItemPosition) {
                                    7 -> {
                                        refreshItem(
                                            UsageStatsManager.INTERVAL_WEEKLY, sTime, eTime, view
                                        )
                                    }
                                    8 -> {
                                        refreshItem(
                                            UsageStatsManager.INTERVAL_MONTHLY, sTime, eTime, view
                                        )
                                    }
                                    9 -> {
                                        refreshItem(
                                            UsageStatsManager.INTERVAL_YEARLY, sTime, eTime, view
                                        )
                                    }
                                }
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
            val ua = t.usageStats
            val sb = UsageStatsClass.usageStatsToString(ua, t.firstInstallTime, t.LastUpdateTime)
            MaterialAlertDialogBuilder(view.context)
                .setTitle(t.appLabel)
                .setMessage(sb)
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

private class PackageValue(
    val appLabel: String,
    val usageStats: UsageStats,
    val appIcon: Drawable?,
    val firstInstallTime: Long,
    val LastUpdateTime: Long,
) {
    constructor(
        appLabel: String,
        usageStats: UsageStats,
        firstInstallTime: Long,
        LastUpdateTime: Long,
    ) : this(appLabel, usageStats, null, firstInstallTime, LastUpdateTime)
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
        val p = StripString.packageNameStrip(Pair(custom.appLabel, custom.usageStats.packageName))
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