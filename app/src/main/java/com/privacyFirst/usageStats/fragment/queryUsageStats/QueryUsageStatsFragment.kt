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
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.privacyFirst.usageStats.Compatible.checkUsageStatsPermission
import com.privacyFirst.usageStats.RecyclerViewAdapter.OnItemClickListener
import com.privacyFirst.usageStats.dymaticLib.string.StripString
import com.privacyFirst.usageStats.staticLib.date.DateTrans
import com.privacyFirst.usageStats.staticLib.slimOrFastestMap.MapDowngradeIntV
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


class QueryUsageStatsFragment : Fragment() {


    private var itemsListMap = MapDowngradeIntV<PackageValue>()

    private val onlyOneDialog = AtomicBoolean()

    private var clickedItem: Int = 3

    //order:1->maxwidth, 2->customAdapter
    //private val maxwidth = AtomicInteger()//not atomic operation just save a value
    private val customAdapter = RecyclerViewAdapter(itemsListMap)

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
            0 -> return "week_range$ap"
            1 -> return "month_range$ap"
            2 -> return "year_range$ap"
        }
        return DateTrans.dateStamp(todayInUtcMilliseconds - (i - 3) * 24 * 60 * 60 * 1000)
    }

    private fun flushChoice() {
        val mTodayInUtcMilliseconds = MaterialDatePicker.todayInUtcMilliseconds()
        if (mTodayInUtcMilliseconds != todayInUtcMilliseconds) {
            todayInUtcMilliseconds = mTodayInUtcMilliseconds
            for (i in 3..9) {
                choices[i] = forEachChoice(i)
            }

        }
    }

    private var theView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = inflater.inflate(R.layout.usagestats_fragment, container, false)
        theView = v
        return v

    }

    override fun onStart() {
        super.onStart()
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.usageStatsRecyclerView)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        customAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                myItemClick(view, position)
            }
        })

        recyclerView.adapter = customAdapter
        val button =
            requireView().findViewById<FloatingActionButton>(R.id.floating_action_button)
        button.setOnClickListener { view ->
            onChangeClick(view)
        }
    }


    override fun onResume() {
        super.onResume()
        if (itemsListMap.isEmpty()) {
            val sTime = todayInUtcMilliseconds - 24 * 60 * 60 * 1000
            val eTime = todayInUtcMilliseconds
            val v = theView ?: throw IllegalStateException("theView is null")
            refreshItem(
                UsageStatsManager.INTERVAL_WEEKLY, sTime, eTime,
                v.findViewById<RecyclerView>(R.id.usageStatsRecyclerView)
            )
        }

    }

    private fun refreshItem(intervalType: Int, sTime: Long, eTime: Long, view: View)  {
        if (!checkUsageStatsPermission(view.context))
            Snackbar.make(view, "Need Permission", Snackbar.LENGTH_LONG)
                .setAction("Set") {
                    val notice =
                        "Select " + getString(R.string.app_name) + " ,then grant the permission."
                    Toast.makeText(it.context, notice, Toast.LENGTH_LONG).show()
                    val int = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivity(int)
                }.show()

        val usageStatsManager = Compatible.getUsageStatsManager(view.context)

        val queryUsageStats =
            usageStatsManager?.queryUsageStats(intervalType, sTime, eTime) ?: emptyList()
        if (queryUsageStats.isNotEmpty()) {
            when (intervalType) {
                UsageStatsManager.INTERVAL_WEEKLY -> {
                    choices[0] =
                        "From " + DateTrans.stamp(queryUsageStats[0].firstTimeStamp)
                }
                UsageStatsManager.INTERVAL_MONTHLY -> {
                    choices[1] =
                        "From " + DateTrans.stamp(queryUsageStats[0].firstTimeStamp)
                }
                UsageStatsManager.INTERVAL_YEARLY -> {
                    choices[2] =
                        "From " + DateTrans.stamp(queryUsageStats[0].firstTimeStamp)
                }
            }
        } else {
            Snackbar.make(view, "No Data", Snackbar.LENGTH_SHORT).show()
        }
        val a = AtomicInteger()
        val writeMap = ConcurrentHashMap<Int, PackageValue>()
        queryUsageStats.parallelStream().filter { i ->
            i.lastTimeUsed / (24 * 60 * 60 * 1000) != 0L || i.totalTimeInForeground != 0L
        }.forEach { usageStats ->

                val appInfoF =
                    try {
                        view.context.packageManager.getApplicationInfo(
                            usageStats.packageName, PackageManager.GET_META_DATA
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }

                val packageInfoF =
                    try {
                        view.context.packageManager.getPackageInfo(
                            usageStats.packageName, PackageManager.GET_META_DATA
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }

                val appInfo = appInfoF
                val packageInfo = packageInfoF
                if (appInfo == null || packageInfo == null) {
                    val value = PackageValue("-uninstalled-", usageStats, 0, 0)
                    writeMap[a.getAndIncrement()] = value
                } else {
                    val appName = appInfo.loadLabel(view.context.packageManager).toString()
                    val icon = appInfo.loadIcon(view.context.packageManager)

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
                        if (checkedItemPosition in 3..9) {
                            val cip = checkedItemPosition - 3
                            val sTime =
                                todayInUtcMilliseconds - (cip) * 24 * 60 * 60 * 1000 + 1
                            val eTime =
                                todayInUtcMilliseconds - (cip - 1) * 24 * 60 * 60 * 1000 - 2
                            refreshItem(UsageStatsManager.INTERVAL_DAILY, sTime, eTime, view)
                        } else {
                            val sTime = todayInUtcMilliseconds - 24 * 60 * 60 * 1000
                            val eTime = todayInUtcMilliseconds - 1
                            if (checkedItemPosition in 0..2) {
                                when (checkedItemPosition) {
                                    0 -> {
                                        refreshItem(
                                            UsageStatsManager.INTERVAL_WEEKLY, sTime, eTime, view
                                        )
                                    }
                                    1 -> {
                                        refreshItem(
                                            UsageStatsManager.INTERVAL_MONTHLY, sTime, eTime, view
                                        )
                                    }
                                    2 -> {
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

private class RecyclerViewAdapter(
    private val itemsList: MapDowngradeIntV<PackageValue>
) :
    RecyclerView.Adapter<RecyclerViewAdapter.CardViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTextView: TextView = view.findViewById(R.id.itemTextView)
        val itemTextView2: TextView = view.findViewById(R.id.itemTextView2)
        val appIcon: ImageView = view.findViewById(R.id.appIcon)
        val cardView: MaterialCardView = view.findViewById(R.id.card)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        if (onItemClickListener != null) {
            mOnItemClickListener = onItemClickListener
        }
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.usagestats_card_view, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
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