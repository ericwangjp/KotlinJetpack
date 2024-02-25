package com.example.kotlinjetpack.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.kotlinjetpack.databinding.ItemCategoryParentBinding
import com.example.kotlinjetpack.databinding.ItemProductCategoryListBinding
import com.example.kotlinjetpack.interfaces.OnItemClickListener
import com.example.kotlinjetpack.model.RightModel

/**
 * desc: ProductCategoryParentAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/7 17:35
 */
class ProductCategoryListAdapter(
    private val dataList: List<RightModel>,
    val mOnItemClickListener: OnItemClickListener?,
    val scrollLeftAndRight: (leftPosition: Int, rightPosition: Int, offset: Int) -> Unit
) : RecyclerView.Adapter<ProductCategoryListAdapter.LinkageListParentHolder>() {

    private var selectedPos: Int = 0

    inner class LinkageListParentHolder(val itemViewBinding: ViewBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        init {
            itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(itemViewBinding.root, bindingAdapterPosition)
            }
            itemView.setOnLongClickListener {
                mOnItemClickListener?.onItemLongClick(itemViewBinding.root, bindingAdapterPosition)
                true
            }
        }

        fun setVisibility(isVisible: Boolean) {
            val param = itemView.layoutParams as RecyclerView.LayoutParams
            if (isVisible) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT
                param.width = LinearLayout.LayoutParams.MATCH_PARENT
            } else {
                param.height = 0
                param.width = 0
            }
            itemView.layoutParams = param
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkageListParentHolder {
        return when (viewType) {
            1 -> LinkageListParentHolder(
                ItemCategoryParentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> LinkageListParentHolder(
                ItemProductCategoryListBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].isTitle) 1 else 0
    }

    override fun onBindViewHolder(holder: LinkageListParentHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> (holder.itemViewBinding as ItemProductCategoryListBinding).tvCategoryTitle.text =
                dataList[position].name
            1 -> (holder.itemViewBinding as ItemCategoryParentBinding).apply {
                tvTitle.text = dataList[position].titleName
                if (selectedPos == position) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#48CE55"))
                    tvTitle.setTextColor(Color.parseColor("#ffffff"))
                } else {
                    holder.itemView.setBackgroundColor(Color.parseColor("#fefdfd"))
                    tvTitle.setTextColor(Color.parseColor("#000000"))
                }
            }
        }


        val currentParentPos = dataList[selectedPos].parentPos
        val nextTitleIndex: Int = dataList.indexOfFirst { it.parentPos == currentParentPos + 1 }
        if (nextTitleIndex != -1) {
            if (getItemViewType(position) == 0) {
                holder.setVisibility(position in selectedPos until nextTitleIndex)
            }
        }

    }

    fun setSelection(position: Int) {
        if (selectedPos == position) return
        val currentParentPos = dataList[selectedPos].parentPos
        val nextTitleIndex: Int = dataList.indexOfFirst { it.parentPos == currentParentPos + 1 }
        if (nextTitleIndex != -1) {
            notifyItemRangeChanged(selectedPos, nextTitleIndex - selectedPos)
        }
        this.selectedPos = position
        val currentNewParentPos = dataList[selectedPos].parentPos
        val nextNewTitleIndex: Int = dataList.indexOfFirst { it.parentPos == currentNewParentPos + 1 }
        if (nextNewTitleIndex != -1) {
            notifyItemRangeChanged(selectedPos, nextNewTitleIndex - selectedPos)
        }
//        notifyDataSetChanged()
    }

}