package com.example.kotlinjetpack.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.kotlinjetpack.databinding.ItemProductCategoryListBinding
import com.example.kotlinjetpack.interfaces.OnItemClickListener
import com.example.kotlinjetpack.model.CategoryTwoArray

/**
 * desc: ProductCategoryListAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/5 18:59
 */
class ProductCategoryChildAdapter(
    private val dataList: ArrayList<CategoryTwoArray> = arrayListOf(),
    val mOnItemClickListener: OnItemClickListener?
) : Adapter<ProductCategoryChildAdapter.LinkageListViewHolder>() {

    private var selectedPos: Int =  0

    inner class LinkageListViewHolder(val itemViewBinding: ItemProductCategoryListBinding) :
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkageListViewHolder {
        return LinkageListViewHolder(
            ItemProductCategoryListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: LinkageListViewHolder, position: Int) {
        holder.itemViewBinding.tvCategoryTitle.text = dataList[position].name
        when (position) {
            selectedPos -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#f3f3f3"))
                holder.itemViewBinding.tvCategoryTitle.setTextColor(Color.parseColor("#0068cf"))
            }
            else -> {
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                holder.itemViewBinding.tvCategoryTitle.setTextColor(Color.parseColor("#1e1d1d"))
            }
        }
    }

    fun setSelection(position: Int) {
        Log.e("fqy-->", "child Selection position: $position")
        Log.e("fqy-->", "child Selection data size: ${dataList.size}")
//        滚动联动修正
        var fixedPos = position
        if (fixedPos >= dataList.size) {
            fixedPos = dataList.size - 1
        }
        if (fixedPos < 0) {
            fixedPos = 0
        }
        Log.e("fqy-->", "child 原始选择位置: $selectedPos")
        Log.e("fqy-->", "child 最终选择位置2: $fixedPos")
        if (selectedPos == fixedPos) return
        notifyItemChanged(selectedPos)
        this.selectedPos = fixedPos
        notifyItemChanged(selectedPos)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(newDataList: List<CategoryTwoArray>) {
        Log.e("fqy-->", "child refresh data: ")
        dataList.clear()
        if (newDataList.isNotEmpty()) {
            dataList.addAll(newDataList)
        }
        notifyDataSetChanged()
    }

}