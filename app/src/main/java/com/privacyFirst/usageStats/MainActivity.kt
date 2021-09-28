package com.privacyFirst.usageStats

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = RootFragment()
        fragmentTransaction.add(R.id.root_fragment_view, fragment)
        fragmentTransaction.commit()
    }
}


class RootFragment : Fragment() {

    private lateinit var rootFragmentAdapter: RootFragmentAdapter
    private lateinit var viewPager2: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.root_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMap = SparseArray<String>(1)
        fragmentMap.append(0, "UsageStats")
        //fragmentMap.append(1, "Inactive&StandbyBucket")
        //fragmentMap.append(2, "EventFragment")

        rootFragmentAdapter = RootFragmentAdapter(this)
        val vp2 = view.findViewById<ViewPager2>(R.id.pager)
        viewPager2 = vp2
        viewPager2.adapter = rootFragmentAdapter

        val tabLayout = requireActivity().findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, vp2) { tab, position ->
            val str = fragmentMap.get(position)
            if (str != null) tab.text = str
            else throw Exception("Unknown position $position")
        }.attach()
    }
}

private class RootFragmentAdapter(
    parentFragment: Fragment,
) : FragmentStateAdapter(parentFragment) {

    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> QueryUsageStatsFragment()
            //1 ->InactiveAndStandbyBucketFragment()
            //2 -> QueryEventsFragment()
            else -> throw Exception("Unknown position $position")
        }

    }
}


