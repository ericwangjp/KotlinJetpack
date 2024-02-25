package com.example.kotlinjetpack.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

/**
 * desc: CommonExt 扩展函数
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/12/1 10:06 上午
 */

/**
 * RecyclerView item 可见性检测
 * percent  列表项可见性阈值
 * 参考：https://mp.weixin.qq.com/s/Knp2ZtF5eoK5I8og3dkeGw
 * https://juejin.cn/post/7165428399282847757
 */
fun RecyclerView.addOnItemVisibilityChangeListener(
    percent: Float = 0.5f, viewGroups: List<ViewGroup> = emptyList(),
    block: (itemView: View, adapterIndex: Int, isVisible: Boolean) -> Unit
) {
    val childVisibleRect = Rect()
    val visibleAdapterIndexs = mutableSetOf<Int>()
    val checkVisibility = {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val adapterIndex = getChildAdapterPosition(child)
            if (adapterIndex == RecyclerView.NO_POSITION) continue
            val isChildVisible = child.getLocalVisibleRect(childVisibleRect)
            val visibleArea = childVisibleRect.let { it.height() * it.width() }
            val realArea = child.width * child.height
            if (this.isInScreen && isChildVisible && visibleArea >= realArea * percent) {
                if (visibleAdapterIndexs.add(adapterIndex)) {
                    block(child, adapterIndex, true)
                }
            } else {
                if (adapterIndex in visibleAdapterIndexs) {
                    block(child, adapterIndex, false)
                    visibleAdapterIndexs.remove(adapterIndex)
                }
            }
        }
    }
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            checkVisibility()
        }
    }
    addOnScrollListener(scrollListener)
    // 为列表添加全局可见性检测
    onVisibilityChange(viewGroups, false) { view, isVisible ->
        // 当列表可见时，检测其表项的可见性
        if (isVisible) {
            checkVisibility()
        } else {
            // 当列表不可见时，回调所有可见表项为不可见
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val adapterIndex = getChildAdapterPosition(child)
                if (adapterIndex in visibleAdapterIndexs) {
                    block(child, adapterIndex, false)
                    visibleAdapterIndexs.remove(adapterIndex)
                }
            }
        }
    }
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
        }

        override fun onViewDetachedFromWindow(v: View) {
            if (v !is RecyclerView) return
            v.removeOnScrollListener(scrollListener)
            removeOnAttachStateChangeListener(this)
        }
    })
}


/**
 * ViewPager2 item 可见性检测
 */
fun ViewPager2.addOnPageVisibilityChangeListener(block: (index: Int, isVisible: Boolean) -> Unit) {
    // 当前页
    var lastPage: Int = currentItem
    // 注册页滚动监听器
    val listener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            // 回调上一页不可见
            if (lastPage != position) {
                block(lastPage, false)
            }
            // 回调当前页可见
            block(position, true)
            lastPage = position
        }
    }
    registerOnPageChangeCallback(listener)
    // 避免内存泄漏
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
        }

        override fun onViewDetachedFromWindow(v: View) {
            if (v !is ViewPager2) return
            if (ViewCompat.isAttachedToWindow(v)) {
                v.unregisterOnPageChangeCallback(listener)
            }
            removeOnAttachStateChangeListener(this)
        }

    })
}


val View.isInScreen: Boolean
    get() = ViewCompat.isAttachedToWindow(this) && visibility == View.VISIBLE && getLocalVisibleRect(
        Rect()
    )

/**
 * view 通用可见性监测方案
 * 参考：https://juejin.cn/post/7165427955902971918
 */
fun View.onVisibilityChange(
    viewGroups: List<ViewGroup> = emptyList(), // 会被插入 Fragment 的容器集合
    needScrollListener: Boolean = true,
    block: (view: View, isVisible: Boolean) -> Unit
) {
    val KEY_VISIBILITY = "KEY_VISIBILITY".hashCode()
    val KEY_HAS_LISTENER = "KEY_HAS_LISTENER".hashCode()
    // 若当前控件已监听可见性，则返回
    if (getTag(KEY_HAS_LISTENER) == true) return

    // 检测可见性
    val checkVisibility = {
        // 获取上一次可见性
        val lastVisibility = getTag(KEY_VISIBILITY) as? Boolean
        // 判断控件是否出现在屏幕中
        val isInScreen = this.isInScreen
        // 首次可见性变更
        if (lastVisibility == null) {
            if (isInScreen) {
                block(this, true)
                setTag(KEY_VISIBILITY, true)
            }
        }
        // 非首次可见性变更
        else if (lastVisibility != isInScreen) {
            block(this, isInScreen)
            setTag(KEY_VISIBILITY, isInScreen)
        }
    }

    // 全局重绘监听器
    class LayoutListener : ViewTreeObserver.OnGlobalLayoutListener {
        // 标记位用于区别是否是遮挡case
        var addedView: View? = null
        override fun onGlobalLayout() {
            // 遮挡 case
            if (addedView != null) {
                // 插入视图矩形区域
                val addedRect = Rect().also { addedView?.getGlobalVisibleRect(it) }
                // 当前视图矩形区域
                val rect = Rect().also { this@onVisibilityChange.getGlobalVisibleRect(it) }
                // 如果插入视图矩形区域包含当前视图矩形区域，则视为当前控件不可见
                if (addedRect.contains(rect)) {
                    block(this@onVisibilityChange, false)
                    setTag(KEY_VISIBILITY, false)
                } else {
                    block(this@onVisibilityChange, true)
                    setTag(KEY_VISIBILITY, true)
                }
            }
            // 非遮挡 case
            else {
                checkVisibility()
            }
        }
    }

    val layoutListener = LayoutListener()
    // 编辑容器监听其插入视图时机
    viewGroups.forEachIndexed { index, viewGroup ->
        viewGroup.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                // 当控件插入，则置标记位
                layoutListener.addedView = child
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {
                // 当控件移除，则置标记位
                layoutListener.addedView = null
            }
        })
    }
    viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    // 全局滚动监听器
    var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null
    if (needScrollListener) {
        scrollListener = ViewTreeObserver.OnScrollChangedListener { checkVisibility() }
        viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }
    // 全局焦点变化监听器
    val focusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
        val lastVisibility = getTag(KEY_VISIBILITY) as? Boolean
        val isInScreen = this.isInScreen
        if (hasFocus) {
            if (lastVisibility != isInScreen) {
                block(this, isInScreen)
                setTag(KEY_VISIBILITY, isInScreen)
            }
        } else {
            if (lastVisibility == true) {
                block(this, false)
                setTag(KEY_VISIBILITY, false)
            }
        }
    }
    viewTreeObserver.addOnWindowFocusChangeListener(focusChangeListener)
    // 为避免内存泄漏，当视图被移出的同时反注册监听器
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
        }

        override fun onViewDetachedFromWindow(v: View) {
            v ?: return
            // 有时候 View detach 后，还会执行全局重绘，为此退后反注册
            post {
                try {
                    v.viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
                } catch (_: java.lang.Exception) {
                    v.viewTreeObserver.removeGlobalOnLayoutListener(layoutListener)
                }
                v.viewTreeObserver.removeOnWindowFocusChangeListener(focusChangeListener)
                if (scrollListener != null) v.viewTreeObserver.removeOnScrollChangedListener(
                    scrollListener
                )
                viewGroups.forEach { it.setOnHierarchyChangeListener(null) }
            }
            removeOnAttachStateChangeListener(this)
        }
    })
    // 标记已设置监听器
    setTag(KEY_HAS_LISTENER, true)
}