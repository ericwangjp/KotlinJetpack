package com.example.kotlinjetpack.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kotlinjetpack.fragment.TabFragment

class MyFragmentPagerAdapter constructor(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val titles = arrayOf("Android", "Ios", "Kotlin", "Flutter", "Web")
    private val fragments =
        arrayOf(
            TabFragment.newInstance(titles[0], ""),
            TabFragment.newInstance(titles[1], ""),
            TabFragment.newInstance(titles[2], ""),
            TabFragment.newInstance(titles[3], ""),
            TabFragment.newInstance(titles[4], "")
        )

//    override fun getCount(): Int {
//        return titles.size
//    }
//
//    override fun getItem(position: Int): Fragment {
//        return fragments[position]
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
////        return super.getPageTitle(position)
//        return titles[position]
//    }

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}