package com.example.kotlinjetpack.activity.commonTest

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.databinding.ActivityTagTextViewBinding
import com.example.kotlinjetpack.utils.dp2px
import com.example.kotlinjetpack.view.CenterImageSpan

/**
 * desc: CommonActivity
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/29 1:59 下午
 */
class TagTextViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTagTextViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagTextViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        val tags: MutableList<String> = ArrayList<String>().apply {
            add("标签一")
            add("标签2")
            add("标签三")
            add("标签4")
            add("标签5")
            add("标签6")
            add("标签7哈哈哈哈哈哈哈哈哈哈哈")
            add("标签8")
        }
        binding.tvTag.setContentAndTag("大功告成!", tags)

//        方式二：
        addTag(binding.tvTagTwo, "这是一段很长很长的文字，怎么处理换行的问题呢？快来看看效果！", "啊哈哈哈")

//        方式三：
        binding.tvTagThree.addTag(
            "这是一段很长很长的文字，怎么处理换行的问题呢？快来看看效果！",
            "啊哈哈哈哈哈",
            R.drawable.shape_tab_bg,
            R.color.purple_200,
            16f, 2
        )

        binding.tvTagFive.addTags(
            "这是一段很长很长的文字，怎么处理换行的问题呢？快来看看效果！",
            arrayListOf("啊哈哈哈哈哈", "你真棒", "你好啊", "然后呢", "很好!"),
            R.drawable.shape_tab_bg,
            R.color.purple_200,
            16f, 2
        )

//        方式四：
        addTagFour()
    }

    /**
     * 方式四
     */
    private fun addTagFour() {
        val text = "# 这是文案这是文案这是文案这是文案这是文案这是文案这是文案这是文案这是文案这是文案这是文案"
        val spanBuilder = SpannableStringBuilder(text)
        //生成标签View
        val tagView = LayoutInflater.from(this).inflate(R.layout.item_tag_view, null)
        val tagTextView = tagView.findViewById<TextView>(R.id.tv_tag)
        //View转bitmap
        tagTextView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        tagTextView.layout(0, 0, tagTextView.measuredWidth, tagTextView.measuredHeight)
        tagTextView.buildDrawingCache()
        val bitmap = tagTextView.drawingCache
        //bitmap转drawable
        val drawable = BitmapDrawable(this.resources, bitmap)
        drawable.setBounds(0, 0, tagTextView.width, tagTextView.height)
        spanBuilder.setSpan(
            CenterImageSpan(drawable),
            0,
            1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTagFour.text = spanBuilder
    }

    /**
     * 方式二
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun addTag(target: TextView, title: String, tag: String) {
        val content = title + tag
        /**
         * 创建TextView对象，设置drawable背景，设置字体样式，设置间距，设置文本等
         * 这里我们为了给TextView设置margin，给其添加了一个父容器LinearLayout。不过他俩都只是new出来的，不会添加进任何布局
         */
        /**
         * 创建TextView对象，设置drawable背景，设置字体样式，设置间距，设置文本等
         * 这里我们为了给TextView设置margin，给其添加了一个父容器LinearLayout。不过他俩都只是new出来的，不会添加进任何布局
         */
        val layout = LinearLayout(this)
        val textView = TextView(this)
        textView.text = tag
        textView.background = resources.getDrawable(R.drawable.shape_tab_bg, this.theme)
        textView.textSize = 12f
        textView.setTextColor(Color.parseColor("#FDA413"))
        textView.includeFontPadding = false
        textView.setPadding(
            12, 0,
            12, 0
        )
//        textView.height = 36
        textView.gravity = Gravity.CENTER_VERTICAL

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // 设置左间距
        layoutParams.leftMargin = 12
        // 设置下间距，简单解决ImageSpan和文本竖直方向对齐的问题
        layoutParams.bottomMargin = 6
        layout.addView(textView, layoutParams)
        /**
         * 第二步，测量，绘制layout，生成对应的bitmap对象
         */
        layout.isDrawingCacheEnabled = true
        layout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        // 给上方设置的margin留出空间
        layout.layout(
            0,
            0,
            textView.measuredWidth + 12,
            textView.measuredHeight
        )
        // 获取bitmap对象
        val bitmap = Bitmap.createBitmap(layout.drawingCache)
        //千万别忘最后一步
        layout.destroyDrawingCache()
        /**
         * 第三步，通过bitmap生成我们需要的ImageSpan对象
         */
        val imageSpan = ImageSpan(this, bitmap)

        /**
         * 第四步将ImageSpan对象设置到SpannableStringBuilder的对应位置
         */
        val ssb = SpannableStringBuilder(content)
        //将尾部tag字符用ImageSpan替换
        ssb.setSpan(imageSpan, title.length, content.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        target.text = ssb
    }

    /**
     * 方式三
     * 添加单个tag
     */
    fun TextView.addTag(
        content: String = " ",
        tag: String = " ",
        @DrawableRes tagBgId: Int = android.R.drawable.ic_secure,
        tagColor: Int = android.R.color.holo_red_dark,
        tagTextSize: Float = 12F,
        bottomPadding: Int = 2
    ) {
        val totalContent = content.plus(" ").plus(tag.ifEmpty { " " })
        val layout = LinearLayout(this.context)
        val tagView = if (tag.isEmpty()) ImageView(this.context).apply {
            setImageResource(tagBgId)
            setPadding(0, 0, 0, this.context.dp2px(dpValue = bottomPadding.toFloat()))

        } else TextView(this.context).apply {
            text = tag
            background = ResourcesCompat.getDrawable(this.context.resources, tagBgId, null)
            textSize = tagTextSize
            setTextColor(ResourcesCompat.getColor(this.context.resources, tagColor, null))
            includeFontPadding = false
            setPadding(
                this.context.dp2px(dpValue = 10f),
                0,
                this.context.dp2px(dpValue = 10f),
                this.context.dp2px(dpValue = bottomPadding.toFloat())
            )
//            height = this.context.dp2px(dpValue = tagTextSize + bottomPadding+2 )
            gravity = Gravity.CENTER
        }

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.addView(tagView, layoutParams)
        layout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        layout.layout(0, 0, tagView.measuredWidth, tagView.measuredHeight)
        layout.setPadding(this.context.dp2px(dpValue = 20f), 0, 0, 0)

        val bitmap = Bitmap.createBitmap(tagView.width, tagView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        tagView.draw(canvas)

        val imageSpan = ImageSpan(this.context, bitmap)
        val ssb = SpannableStringBuilder(totalContent)
        ssb.setSpan(
            imageSpan,
            content.length + 1,
            totalContent.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        this.text = ssb
    }

    /**
     * 方式三
     * 添加多个tag
     */
    fun TextView.addTags(
        content: String = " ",
        tags: List<String> = arrayListOf(),
        @DrawableRes tagBgId: Int = android.R.drawable.ic_secure,
        tagColor: Int = android.R.color.holo_red_dark,
        tagTextSize: Float = 12F,
        bottomPadding: Int = 2
    ) {
        val totalContent =
            content.plus(" ").plus(if (tags.isEmpty()) " " else tags.joinToString(" "))
        val ssb = SpannableStringBuilder(totalContent)
        for (i in tags.indices) {
            val layout = LinearLayout(this.context)
            val tagView = if (tags.isEmpty()) ImageView(this.context).apply {
                setImageResource(tagBgId)
                setPadding(0, 0, 0, this.context.dp2px(dpValue = bottomPadding.toFloat()))

            } else TextView(this.context).apply {
                text = tags[i]
                background = ResourcesCompat.getDrawable(this.context.resources, tagBgId, null)
                textSize = tagTextSize
                setTextColor(ResourcesCompat.getColor(this.context.resources, tagColor, null))
                includeFontPadding = false
                setPadding(
                    this.context.dp2px(dpValue = 10f),
                    0,
                    this.context.dp2px(dpValue = 10f),
                    this.context.dp2px(dpValue = bottomPadding.toFloat())
                )
//            height = this.context.dp2px(dpValue = tagTextSize + bottomPadding+2 )
                gravity = Gravity.CENTER
                setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this.context, R.drawable.icon_pic_default),
                    null,
                    null,
                    null
                )
            }

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layout.addView(tagView, layoutParams)
            layout.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            layout.layout(0, 0, tagView.measuredWidth, tagView.measuredHeight)
            layout.setPadding(this.context.dp2px(dpValue = 20f), 0, 0, 0)

            val bitmap = Bitmap.createBitmap(tagView.width, tagView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            tagView.draw(canvas)

            val imageSpan = ImageSpan(this.context, bitmap)
//            val startIndex = content.length + getLastLength(tags, i) + i
            val startIndex = content.length + tags.take(i).sumOf { it.length } + i
            val endIndex = startIndex + tags[i].length
            ssb.setSpan(
                imageSpan,
                startIndex + 1,
                endIndex + 1,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        this.text = ssb
    }

    fun getLastLength(list: List<String>, maxLength: Int): Int {
        var length = 0
        for (i in 0 until maxLength) {
            length += list[i].length
        }
        Log.e("getLastLength 1: ", "-> $length")
        Log.e("getLastLength 2: ", "-> ${list.take(maxLength).sumOf { it.length }}")
        return length
    }
}