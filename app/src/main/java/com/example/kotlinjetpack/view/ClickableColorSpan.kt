package com.example.kotlinjetpack.view

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * desc: ClickableColorSpan$
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/5/25$ 10:31 下午$
 */
class ClickableColorSpan(val color: Int, val action: (() -> Unit)?) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = color
        ds.isUnderlineText = false;//是否显示下划线
    }

    override fun onClick(widget: View) {
        action?.invoke()
    }
}

//
//val name = "名字名字名字"
//val content = "内容内容内容内容内容内容"
//val spannableString = SpannableString(" ${name} ${content}")
//var index = 0
//
//val drawable = ContextCompat.getDrawable(this, R.drawable.circle_red)
//val height = resources?.getDimensionPixelOffset(R.dimen.calces_36px) ?: 0  //图片高
//val width = resources?.getDimensionPixelOffset(R.dimen.calces_90px) ?: 0  //图片宽度
//drawable?.setBounds(0, 0, width, height)
//if (drawable != null) {
//    spannableString.setSpan(
//        CenterImageSpan(drawable),
//        0,
//        1,
//        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//    )
//    index = 1
//}
//
//val spanName = ClickableColorSpan(Color.parseColor("#8CE4FA")) {
//    Toast.makeText(this@MainActivity, "点击了名字", Toast.LENGTH_LONG).show()
//}
//spannableString.setSpan(
//spanName,
//index,
//index + name.length,
//Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//)
//index += name.length + 1
//
//val spanContent = ClickableColorSpan(Color.parseColor("#FACE16")) {
//    Toast.makeText(this@MainActivity, "点击了内容", Toast.LENGTH_LONG).show()
//}
//spannableString.setSpan(
//spanContent,
//index,
//spannableString.length,
//Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//)
//
//mTvSpan?.text = spannableString
//mTvSpan?.movementMethod = LinkMovementMethod.getInstance();