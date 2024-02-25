package com.example.kotlinjetpack.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewbinding.ViewBinding
import com.example.kotlinjetpack.databinding.ItemCategoryContentBinding
import com.example.kotlinjetpack.databinding.ItemCategoryContentTitleBinding
import com.example.kotlinjetpack.databinding.LayoutContentListFooterBinding
import com.example.kotlinjetpack.interfaces.OnItemClickListener
import com.example.kotlinjetpack.model.RightModel

/**
 * desc: ProductCategoryContentAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/5 18:59
 */
class ProductCategoryContentAdapter(
    private val dataList: List<RightModel>, val mOnItemClickListener: OnItemClickListener?
) : Adapter<ProductCategoryContentAdapter.ProductContentListViewHolder>() {


    inner class ProductContentListViewHolder(val itemViewBinding: ViewBinding) :
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
                param.height = RecyclerView.LayoutParams.WRAP_CONTENT
                param.width = RecyclerView.LayoutParams.MATCH_PARENT
            } else {
                param.height = 0
                param.width = 0
            }
            itemView.layoutParams = param
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ProductContentListViewHolder {
        return when (viewType) {
            0 -> ProductContentListViewHolder(
                ItemCategoryContentTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            1 -> ProductContentListViewHolder(
                ItemCategoryContentBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> ProductContentListViewHolder(
                LayoutContentListFooterBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return dataList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == dataList.size) {
            2
        } else if (dataList[position].isTitle) 0 else 1
    }

    override fun onBindViewHolder(holder: ProductContentListViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> (holder.itemViewBinding as ItemCategoryContentTitleBinding).tvTitle.text =
                dataList[position].name
            1 -> (holder.itemViewBinding as ItemCategoryContentBinding).apply {
                tvTitle.text = "${dataList[position].name} - $position"
                sdvImg.setImageURI(Uri.parse(dataList[position].imgSrc), this)
            }
            2 -> (holder.itemViewBinding as LayoutContentListFooterBinding).tvDesc.text =
                "上滑查看下一个产品[婚纱照]"
        }

        holder.setVisibility(position != 0)
    }


}