package com.example.kotlinjetpack.activity.linkageScroll

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.adapter.LifeRecyclerAdapter
import com.example.kotlinjetpack.databinding.ActivityCollapseExpandHeadBinding
import com.example.kotlinjetpack.model.LifeItem
import com.example.kotlinjetpack.utils.px2dp
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlin.math.abs

/**
 * desc: 仿旧版支付宝折叠头部
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/7/18 10:30
 */
class CollapseExpandHeadActivity : AppCompatActivity(), OnOffsetChangedListener {
    private lateinit var binding: ActivityCollapseExpandHeadBinding
    private val TAG = "ScrollAlipayActivity"

    // 分别声明伸展时候与收缩时候的工具栏视图
    private var tl_expand: View? = null
    private var tl_collapse: View? = null

    // 分别声明三个遮罩视图
    private var v_expand_mask: View? = null
    private var v_collapse_mask: View? = null
    private var v_pay_mask: View? = null

    // 遮罩颜色
    private var mMaskColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollapseExpandHeadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        // 获取默认的蓝色遮罩颜色
        mMaskColor = -0xe67c2f
        // 从布局文件中获取名叫rv_content的循环视图
        val rv_content: RecyclerView = findViewById(R.id.rv_content)
        // 设置循环视图的布局管理器（四列的网格布局管理器）
        rv_content.setLayoutManager(GridLayoutManager(this, 4))
        // 给rv_content设置生活频道网格适配器
        rv_content.adapter = LifeRecyclerAdapter(this, LifeItem(1, "默认值").getDefaultData())
        // 从布局文件中获取名叫abl_bar的应用栏布局
        val abl_bar = findViewById<AppBarLayout>(R.id.abl_bar)
        // 从布局文件中获取伸展之后的工具栏视图
        tl_expand = findViewById<View>(R.id.tl_expand)
        // 从布局文件中获取收缩之后的工具栏视图
        tl_collapse = findViewById<View>(R.id.tl_collapse)
        // 从布局文件中获取伸展之后的工具栏遮罩视图
        v_expand_mask = findViewById<View>(R.id.v_expand_mask)
        // 从布局文件中获取收缩之后的工具栏遮罩视图
        v_collapse_mask = findViewById<View>(R.id.v_collapse_mask)
        // 从布局文件中获取生活频道的遮罩视图
        v_pay_mask = findViewById<View>(R.id.v_pay_mask)
        // 给abl_bar注册一个位置偏移的监听器
        abl_bar.addOnOffsetChangedListener(this)
    }


    // 每当应用栏向上滚动或者向下滚动，就会触发位置偏移监听器的onOffsetChanged方法
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val offset = abs(verticalOffset)
        // 获取应用栏的整个滑动范围，以此计算当前的位移比例
        val total = appBarLayout!!.totalScrollRange
        val alphaIn: Int = px2dp(offset.toFloat()) * 2
        val alphaOut = if (200 - alphaIn < 0) 0 else 200 - alphaIn
        // 计算淡入时候的遮罩透明度
        val maskColorIn: Int = Color.argb(
            alphaIn, Color.red(mMaskColor),
            Color.green(mMaskColor), Color.blue(mMaskColor)
        )
        // 工具栏下方的生活频道布局要加速淡入或者淡出
        val maskColorInDouble: Int = Color.argb(
            alphaIn * 2, Color.red(mMaskColor),
            Color.green(mMaskColor), Color.blue(mMaskColor)
        )
        // 计算淡出时候的遮罩透明度
        val alphaOutFinal = if (alphaOut * 3 > 255) 255 else alphaOut * 3
        val maskColorOut: Int = Color.argb(
            alphaOutFinal, Color.red(mMaskColor),
            Color.green(mMaskColor), Color.blue(mMaskColor)
        )
        if (offset < total * 0.45) { // 偏移量小于一半，则显示伸展时候的工具栏
            tl_expand!!.visibility = View.VISIBLE
            tl_collapse!!.visibility = View.GONE
            v_expand_mask!!.setBackgroundColor(maskColorInDouble)
        } else { // 偏移量大于一半，则显示收缩时候的工具栏
            tl_expand!!.visibility = View.GONE
            tl_collapse!!.visibility = View.VISIBLE
            Log.i("aaaaaaaaaaa", "显示:$alphaOutFinal")
            v_collapse_mask!!.setBackgroundColor(maskColorOut)
        }
        // 设置life_pay.xml即生活频道视图的遮罩颜色
        v_pay_mask!!.setBackgroundColor(maskColorIn)
    }
}