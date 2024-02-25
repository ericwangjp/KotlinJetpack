package com.example.kotlinjetpack.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

/**
 * desc: ParallaxScrollMotionLayout
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/28 9:08 下午
 */
class ParallaxScrollMotionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val progressVal = -verticalOffset / appBarLayout?.totalScrollRange?.toFloat()!!
        Log.e("ParallaxScroll", "progress:$progressVal")
        progress = progressVal

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as? AppBarLayout)?.addOnOffsetChangedListener(this)
    }
}