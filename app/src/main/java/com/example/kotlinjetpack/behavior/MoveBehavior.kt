package com.example.kotlinjetpack.behavior

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import com.example.kotlinjetpack.view.exercise.MoveView

class MoveBehavior(val context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<MoveView>(context, attrs) {

    override fun getScrimColor(parent: CoordinatorLayout, child: MoveView): Int {
//        return super.getScrimColor(parent, child)
        return Color.YELLOW
    }

    override fun getScrimOpacity(parent: CoordinatorLayout, child: MoveView): Float {
//        return super.getScrimOpacity(parent, child)
        return 0.5f
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: MoveView): Parcelable? {
//        return super.onSaveInstanceState(parent, child)
        return bundleOf(
            "aa" to "博主带你上高速!",
            "string_list" to arrayListOf("a", "n", "d", "r", "o", "i", "d"),
            "PARCELABLE" to super.onSaveInstanceState(parent, child),
        )
    }

    override fun onRestoreInstanceState(
        parent: CoordinatorLayout,
        child: MoveView,
        state: Parcelable
    ) {
//        super.onRestoreInstanceState(parent, child, state)
        (state as? Bundle)?.apply {
            super.onRestoreInstanceState(parent, child, getParcelable("PARCELABLE") ?: return)
        }
    }
}