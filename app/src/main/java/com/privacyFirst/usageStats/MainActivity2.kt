package com.example.myapplicationtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.privacyFirst.usageStats.MyFragment
import com.privacyFirst.usageStats.R


class MainActivity : AppCompatActivity() {

    private val a= arrayOf(1,2,3,4,5)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        layoutInflater.inflate(R.layout.fragment, findViewById<FragmentContainerView>(R.id.fragment_view), false)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = MyFragment()
        fragmentTransaction.add(R.id.fragment_view, fragment)
        fragmentTransaction.commit()
    }
}