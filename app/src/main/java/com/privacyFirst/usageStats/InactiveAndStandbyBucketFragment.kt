package com.privacyFirst.usageStats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.privacyFirst.usageStats.staticLib.slimOrFastestMap.MapDowngradeIntV

class InactiveAndStandbyBucketFragment:Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var thisView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.inactive_and_standby_bucket_fragment, container, false)
        thisView = v
        return v
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

}

private class IASBRecyclerViewAdapter(
    private val itemsList: MapDowngradeIntV<String>
) :
    RecyclerView.Adapter<IASBRecyclerViewAdapter.CardViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}