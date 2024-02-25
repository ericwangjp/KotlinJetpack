package com.example.kotlinjetpack.adapter

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

/**
 * desc: TabPagerAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/1 11:21
 */
class TabPagerAdapter(private val mDataList: List<String>) : PagerAdapter() {
    override fun getCount(): Int {
        return mDataList.size
    }

    override fun isViewFromObject(view: View, objectView: Any): Boolean {
        return view == objectView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val textView = TextView(container.context)
        textView.setText(mDataList[position])
        textView.setGravity(Gravity.CENTER)
        textView.setTextColor(Color.BLACK)
        textView.setTextSize(24f)
        container.addView(textView)
        return textView
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View?)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mDataList[position]
    }

    override fun getItemPosition(view: Any): Int {
        val textView = view as TextView
        val text = textView.text.toString()
        val index = mDataList.indexOf(text)
        return if (index >= 0) {
            index
        } else POSITION_NONE
    }
}