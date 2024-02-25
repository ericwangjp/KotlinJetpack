package com.example.kotlinjetpack.activity.linkageScroll

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.databinding.ActivityScrollFoldAnimBinding
import com.example.kotlinjetpack.utils.ViewWrapper
import com.example.kotlinjetpack.utils.dp2px

class ScrollFoldAnimActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityScrollFoldAnimBinding
    private val TAG = "ScrollFoldAnimActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollFoldAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnExpand.setOnClickListener(this)
        binding.btnCollapse.setOnClickListener(this)
        binding.btnAnimExpand.setOnClickListener(this)
        binding.btnAnimCollapse.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_expand -> expandView()
            R.id.btn_collapse -> collapseView()
            R.id.btn_anim_expand -> expandAnim()
            R.id.btn_anim_collapse -> collapseAnim()
        }

    }

    private fun collapseView() {
        val layoutParams: LinearLayout.LayoutParams =
            binding.laySearch.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = dp2px(0f)
        layoutParams.setMargins(dp2px(0f), dp2px(0f), dp2px(0f), dp2px(0f))
        binding.laySearch.layoutParams = layoutParams
        beginDelayedTransition(binding.laySearch)
    }

    private fun expandView() {
        val layoutParams = binding.laySearch.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = dp2px(200f)
        layoutParams.setMargins(dp2px(0f), dp2px(0f), dp2px(0f), dp2px(0f))
        binding.laySearch.setPadding(14, 0, 14, 0)
        binding.laySearch.layoutParams = layoutParams
        beginDelayedTransition(binding.laySearch)
    }

    private fun beginDelayedTransition(view: ViewGroup) {
        val autoTransition = AutoTransition()
        autoTransition.duration = 200
        TransitionManager.beginDelayedTransition(view, autoTransition)
    }

    private fun expandAnim() {
        val viewWrapper = ViewWrapper(binding.rlayoutAnim)
        val scaleAnimator: ObjectAnimator = ObjectAnimator.ofInt(viewWrapper, "Height", 0, dp2px(200f))
//        val scaleAnimator: ObjectAnimator = ObjectAnimator.ofFloat(binding.rlayoutAnim, "scaleY", 1f, 1.5f)
        val alphaAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(binding.rlayoutAnim, "alpha", 0.0f, 1.0f)
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleAnimator).with(alphaAnimator)
        animatorSet.duration = 500
        animatorSet.start()
    }


    private fun collapseAnim() {
        val viewWrapper = ViewWrapper(binding.rlayoutAnim)
        val scaleAnimator: ObjectAnimator = ObjectAnimator.ofInt(viewWrapper, "Height", dp2px(200f), 0)
//        val scaleAnimator: ObjectAnimator = ObjectAnimator.ofFloat(binding.rlayoutAnim, "scaleY", 1.5f, 1f)
        val alphaAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(binding.rlayoutAnim, "alpha", 1.0F, 0.0F)
        val animatorSet = AnimatorSet()
        animatorSet.play(scaleAnimator).with(alphaAnimator)
        animatorSet.duration = 500
        animatorSet.start()
    }

}