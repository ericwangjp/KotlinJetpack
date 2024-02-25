package com.example.kotlinjetpack.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemUploadPicBinding
import com.example.kotlinjetpack.model.PicInfo

class UploadPicAdapter(private val dataLists: MutableList<PicInfo>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_PIC = 1
        const val ITEM_TYPE_ADD = 2
        const val MAX_ITEM_COUNT = 9
    }

    inner class PicViewHolder(itemView: ItemUploadPicBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    inner class AddViewHolder(itemView: ItemUploadPicBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_ADD -> AddViewHolder(
                ItemUploadPicBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> PicViewHolder(
                ItemUploadPicBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var itemViewType = holder.itemViewType
        if (itemViewType == ITEM_TYPE_PIC) {
            holder.itemView.visibility = View.VISIBLE
            dataLists?.let {
                if (holder is PicViewHolder) {
                    holder.binding.imgPic.setImageResource(it[position].id)
                    holder.binding.tvDesc.text = it[position].desc
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
//        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (holder.itemViewType == ITEM_TYPE_PIC) {
                holder.itemView.visibility = View.VISIBLE
            } else if (holder.itemViewType == ITEM_TYPE_ADD) {
                onBindViewHolder(holder, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return when {
            dataLists == null -> 1
            dataLists.size >= MAX_ITEM_COUNT -> MAX_ITEM_COUNT
            else -> dataLists.size + 1
        }
    }

    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
        if (dataLists == null) {
            return ITEM_TYPE_ADD
        }
        return if (dataLists.size >= MAX_ITEM_COUNT) {
            ITEM_TYPE_PIC
        } else {
            if (position == dataLists.size) {
                ITEM_TYPE_ADD
            } else {
                ITEM_TYPE_PIC
            }
        }

    }

    fun getDataList() = dataLists

    fun removeDragData(position: Int) {
        if (dataLists == null) {
            return
        }
        if (position < 0 || position > dataLists.size) {
            return
        }
        Log.e("删除的位置: ","-》$position" )
        dataLists.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position, "payload")
    }
}