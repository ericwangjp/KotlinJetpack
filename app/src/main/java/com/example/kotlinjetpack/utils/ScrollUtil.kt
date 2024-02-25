package com.example.kotlinjetpack.utils

import android.view.View
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ScrollingView
import androidx.viewpager.widget.ViewPager

object ScrollUtil {
    fun isScrollableView(view: View?): Boolean {
        return view is AbsListView
                || view is ScrollView
                || view is ScrollingView
                || view is WebView
                || view is NestedScrollingChild
    }

    fun isContentView(view: View?): Boolean {
        return isScrollableView(view)
                || view is ViewPager
                || view is NestedScrollingParent
    }

}