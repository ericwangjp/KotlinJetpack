package com.example.kotlinjetpack.activity.commonTest

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.databinding.ActivityDianZanBinding
import com.example.kotlinjetpack.utils.ViewLikeBesselUtils
import com.example.kotlinjetpack.utils.ViewLikeUtils
import com.example.kotlinjetpack.view.DivergeView


class DianZanActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDianZanBinding
    private val imgs by lazy {
        arrayListOf<View>()
    }
    private val imageView by lazy {
        ImageView(this).apply {
            setImageResource(R.drawable.ic_red_heart)
        }
    }
    private val animator by lazy {
        ValueAnimator.ofInt(10, 200).apply {
            duration = 800
        }
    }
    private val mList by lazy {
        arrayListOf<Bitmap>().apply {
            // 添加点赞图标
            add(
                (AppCompatResources.getDrawable(
                    this@DianZanActivity,
                    R.drawable.ic_red_heart
                ) as BitmapDrawable).bitmap
            )

            add(
                (AppCompatResources.getDrawable(
                    this@DianZanActivity,
                    R.drawable.ic_red_heart
                ) as BitmapDrawable).bitmap
            )

        }
    }
    private var mIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDianZanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnOne.setOnClickListener(this)
        binding.btnTwo.setOnClickListener(this)
        binding.btnThree.setOnClickListener(this)
        binding.btnFour.setOnClickListener(this)
        binding.btnFive.setOnClickListener(this)
        binding.btnSix.setOnClickListener(this)
        binding.btnSeven.setOnClickListener(this)


        for (i in 0..100) {
            imgs.add(ImageView(this).apply { setImageResource(R.drawable.ic_red_heart) })
        }

        binding.divergeView.post(Runnable {
            binding.divergeView.setEndPoint(PointF(binding.divergeView.measuredWidth / 2f, 0f))
            binding.divergeView.setDivergeViewProvider(Provider())
        })
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnOne -> {
                ViewLikeUtils(
                    binding.btnOne,
                    imageView,
                    object : ViewLikeUtils.ViewLikeClickListener {
                        override fun onClick(
                            view: View?,
                            toggle: Boolean,
                            viewLikeUtils: ViewLikeUtils?
                        ) {
                            viewLikeUtils?.startLikeAnim(animator)
                        }
                    })
            }
            binding.btnTwo -> {
                ViewLikeBesselUtils(
                    binding.btnTwo,
                    imgs,
                    object : ViewLikeBesselUtils.ViewLikeClickListener {
                        override fun onClick(
                            view: View?,
                            toggle: Boolean,
                            viewLikeBesselUtils: ViewLikeBesselUtils?
                        ) {
                            viewLikeBesselUtils?.startLikeAnim()
                        }

                    })
            }
            binding.btnThree -> {
                binding.flowLikeView.addLikeView()
            }
            binding.btnFour -> {
                if (mIndex == mList.size) {
                    mIndex = 0;
                }
                binding.divergeView.startDiverges(mIndex)
                mIndex++

            }
            binding.btnFive -> {
                binding.likeView.setOnLikeListeners { isCancel ->
                    Toast.makeText(this, "点击了，是否取消 ：$isCancel", Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnSix -> {
                binding.sbHeart.init(this)
                binding.sbLike.init(this)
                binding.sbSmile.init(this)
                binding.sbStar.init(this)
            }
            binding.btnSeven -> {
                setResult(RESULT_OK, Intent().putExtra("result","页面传值:${SystemClock.elapsedRealtime()}"))
                finish()
            }
        }
    }

    inner class Provider : DivergeView.DivergeViewProvider {
        override fun getBitmap(var1: Any?): Bitmap? {
            return mList[var1 as Int]
        }
    }

}