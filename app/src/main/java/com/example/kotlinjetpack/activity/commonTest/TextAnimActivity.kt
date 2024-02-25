package com.example.kotlinjetpack.activity.commonTest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.databinding.ActivityTextAnimBinding
import kotlin.random.Random

class TextAnimActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextAnimBinding
    private var isAnimation = false
    private var tipViewWidth = 498

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.tvAnimationText.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @Override
            override fun onGlobalLayout() {
                binding.tvAnimationText.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val textHeight = binding.tvAnimationText.height
                Log.e("tag", "textHeight: $textHeight");

                //一开始需要先让text往上移动它自身的高度
                binding.tvAnimationText.translationY = -textHeight.toFloat()
                Log.e("tag", "top:" + binding.tvAnimationText.top)
                //再以动画的形式慢慢滚动下拉
                binding.tvAnimationText.animate().translationYBy(textHeight.toFloat())
                    .setDuration(500)
                    .setStartDelay(1000).start()

            }
        })
        binding.btnChangeText.setOnClickListener {
            var text = "长度"
            for (item in 0..Random.nextInt(1, 10)) {
                text += "$item"
            }
            binding.tvAnimation.text = text
            binding.tvAnimation.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            tipViewWidth = binding.tvAnimation.measuredWidth
            Log.e("tipViewWidth: ", "$tipViewWidth")
            val layoutParams = binding.tvAnimation.layoutParams
            layoutParams.width = binding.tvAnimation.measuredWidth
            binding.tvAnimation.layoutParams = layoutParams
        }
        binding.btnStartAnimator.setOnClickListener {
            val openAnimator = ValueAnimator.ofInt(0, tipViewWidth).apply {
                duration = 500

                addUpdateListener {
                    val animatedValue = it.animatedValue as Int
                    Log.e("展开值变化：", "-->$animatedValue")
                    val layoutParams = binding.tvAnimation.layoutParams as LinearLayout.LayoutParams
                    layoutParams.width = animatedValue
                    binding.tvAnimation.layoutParams = layoutParams
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        Log.e("展开动画执行结束：", "-->${binding.tvAnimation.width}")
                        Handler().postDelayed(Runnable {
                            Log.e("展开动画执行结束2：", "-->${binding.tvAnimation.width}")
                        }, 2000)
                    }
                })
            }

            val closeAnimator = ValueAnimator.ofInt(tipViewWidth, 0).apply {
                duration = 500

                addUpdateListener {
                    val animatedValue = it.animatedValue as Int
                    Log.e("折叠值变化：", "-->$animatedValue")
                    val layoutParams = binding.tvAnimation.layoutParams as LinearLayout.LayoutParams
                    layoutParams.width = animatedValue
                    binding.tvAnimation.layoutParams = layoutParams
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        Log.e("折叠动画执行结束：", "-->${binding.tvAnimation.width}")
                        Handler().postDelayed(kotlinx.coroutines.Runnable {
                            Log.e("折叠动画执行结束2：", "-->${binding.tvAnimation.width}")
                        }, 2000)
                    }
                })

            }
            if (!isAnimation) {
                openAnimator.start()
            } else {
                closeAnimator.start()
            }
            isAnimation = !isAnimation
        }
    }
}