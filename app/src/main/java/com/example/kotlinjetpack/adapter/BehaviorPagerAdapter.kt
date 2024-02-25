package com.example.kotlinjetpack.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kotlinjetpack.fragment.BehaviorFragment

class BehaviorPagerAdapter constructor(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val titles = arrayOf("Home", "Community", "Mine")
    private val fragments =
        arrayOf(
            BehaviorFragment.newInstance(titles[0], ""),
            BehaviorFragment.newInstance(titles[1], ""),
            BehaviorFragment.newInstance(titles[2], "")
        )

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}