package com.example.kotlinjetpack.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemCategoryParentBinding
import com.example.kotlinjetpack.interfaces.OnItemClickListener
import com.example.kotlinjetpack.model.CategoryOneArray
import com.example.kotlinjetpack.model.CategoryTwoArray
import com.scwang.smart.refresh.layout.util.SmartUtil.dp2px

/**
 * desc: ProductCategoryParentAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/7 17:35
 */
class ProductCategoryParentAdapter(
    private val dataList: List<CategoryOneArray>,
    val mOnItemClickListener: OnItemClickListener?,
    val scrollLeftAndRight: (leftPosition: Int, rightPosition: Int, offset: Int) -> Unit
) : RecyclerView.Adapter<ProductCategoryParentAdapter.LinkageListParentHolder>() {

    private var selectedPos: Int = 0

    inner class LinkageListParentHolder(val itemViewBinding: ItemCategoryParentBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        init {
            itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(itemViewBinding.root, bindingAdapterPosition)
            }
            itemView.setOnLongClickListener {
                mOnItemClickListener?.onItemLongClick(itemViewBinding.root, bindingAdapterPosition)
                true
            }
            itemViewBinding.rvCategoryChildList.apply {
                isNestedScrollingEnabled = false
                layoutManager = object : LinearLayoutManager(context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkageListParentHolder {
        return LinkageListParentHolder(
            ItemCategoryParentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: LinkageListParentHolder, parentPosition: Int) {
        holder.itemViewBinding.tvTitle.text = "${dataList[parentPosition].name}-$parentPosition"
        if (holder.itemViewBinding.rvCategoryChildList.adapter == null) {
            holder.itemViewBinding.rvCategoryChildList.adapter =
                ProductCategoryChildAdapter(arrayListOf<CategoryTwoArray>().apply {
                    addAll(dataList[parentPosition].categoryTwoArray)
                }, object : OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        (holder.itemViewBinding.rvCategoryChildList.adapter as ProductCategoryChildAdapter).setSelection(
                            position
                        )
                        Log.e("fqy-->", "onItemClick: ${holder.bindingAdapterPosition} - $position")
                        var count = 0
                        for (i in 0 until holder.bindingAdapterPosition) {
                            count += dataList[i].categoryTwoArray.size
                        }
                        count += position + holder.bindingAdapterPosition + 1
                        Log.e("onItemClick: ", "右侧列表位置：$count")
                        scrollLeftAndRight(holder.bindingAdapterPosition, count, 0)
                    }

                    override fun onItemLongClick(view: View?, position: Int) {

                    }
                })
        } else {
            (holder.itemViewBinding.rvCategoryChildList.adapter as ProductCategoryChildAdapter).refreshData(
                dataList[parentPosition].categoryTwoArray
            )
        }
        when (parentPosition) {
            selectedPos -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#48CE55"))
                holder.itemViewBinding.tvTitle.setTextColor(Color.parseColor("#ffffff"))
                holder.itemViewBinding.rvCategoryChildList.visibility = View.VISIBLE
            }
            else -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#fefdfd"))
                holder.itemViewBinding.tvTitle.setTextColor(Color.parseColor("#000000"))
                holder.itemViewBinding.rvCategoryChildList.visibility = View.GONE
            }
        }
    }

    fun setSelection(position: Int) {
        if (selectedPos == position) return
        Log.e("fqy-->", "parent setSelection refresh: ")
        notifyItemChanged(selectedPos)
        this.selectedPos = position
        notifyItemChanged(selectedPos)
    }

}