package com.example.kotlinjetpack.activity.commonTest

import android.os.Bundle
import android.transition.TransitionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.databinding.ActivityMotionLayout4Binding
import com.example.kotlinjetpack.databinding.ActivityMotionLayout5Binding

class MotionLayoutActivity : AppCompatActivity() {
    //    private lateinit var binding: ActivityMotionLayoutBinding
    private lateinit var binding: ActivityMotionLayout5Binding

    // 定义起始状态的 ConstraintSet （clContainer顶层容器）
    private val startConstraintSet = ConstraintSet()

    // 定义结束状态的 ConstraintSet
    val endConstraintSet = ConstraintSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMotionLayout5Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
//        配合 ActivityMotionLayout3Binding 使用
//        startConstraintSet.clone(binding.clContainer)
//        endConstraintSet.clone(binding.clContainer)
//        endConstraintSet.connect(
//            R.id.button, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16
//        )
//        endConstraintSet.setHorizontalBias(R.id.button, 1f)
//        binding.clContainer.postDelayed({
//            TransitionManager.beginDelayedTransition(binding.clContainer)
//            endConstraintSet.applyTo(binding.clContainer)
//        }, 1000)
    }
}