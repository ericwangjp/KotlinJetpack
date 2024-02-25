package com.example.kotlinjetpack.view

import android.content.Context
import android.graphics.Bitmap
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.example.kotlinjetpack.databinding.ItemTagViewBinding

/**
 * desc: TagTextView
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/25 10:23 上午
 */
class TagTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val contentBuffer: StringBuffer by lazy {
        StringBuffer()
    }

    fun setContentAndTag(content: String, tags: List<String>) {
        contentBuffer.setLength(0)
        contentBuffer.append(content)
        contentBuffer.append(tags.joinToString(""))
        println("contentBuffer-->$contentBuffer")
        val spannableString = SpannableString(contentBuffer)
        for (i in tags.indices) {
            val item = tags[i]
            val itemBinding = ItemTagViewBinding.inflate(LayoutInflater.from(context), null, false)
            itemBinding.tvTag.text = item
            val convertToBitmap = convertToBitmap(itemBinding.root)
//            val bitmapDrawable = BitmapDrawable(resources, convertToBitmap)
//            bitmapDrawable.setBounds(0, 0, itemBinding.tvTag.width, itemBinding.tvTag.height)
//            val imageSpan = ImageSpan(context, convertToBitmap, ImageSpan.ALIGN_BOTTOM)
            val imageSpan = CenterImageSpan(context, convertToBitmap, ImageSpan.ALIGN_BOTTOM)
            val startIndex = content.length + getLastLength(tags, i)
            val endIndex = startIndex + item.length
            spannableString.setSpan(
                imageSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        text = spannableString
        gravity = Gravity.CENTER_VERTICAL
    }

    fun convertToBitmap(view: View): Bitmap {
        view.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        return view.drawingCache
    }

    fun getLastLength(list: List<String>, maxLength: Int): Int {
        var length = 0
        for (i in 0 until maxLength) {
            length += list[i].length
        }
        return length
    }
}