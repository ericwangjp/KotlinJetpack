package com.example.kotlinjetpack.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.LayoutTabIndicatorBinding

/**
 * desc: RcvIndicatorAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/4 2:42 下午
 */
class RcvIndicatorAdapter(val data: List<String>) :
    RecyclerView.Adapter<RcvIndicatorAdapter.MyViewHolder>() {

    private var selectedPos = -1
    private var mOnItemClickListener: OnItemClickListener? = null
    private var dataLists: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutTabIndicatorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvTitle.text = data[position]

        if (selectedPos == position) {
            holder.binding.tvTitle.apply {
                setTextColor(Color.RED)
                paint.isFakeBoldText = true
            }
        } else {
            holder.binding.tvTitle.apply {
                setTextColor(Color.BLACK)
                paint.isFakeBoldText = false
            }
        }

        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onItemClick(
                holder.itemView, holder.absoluteAdapterPosition
            )
        }
        holder.itemView.setOnLongClickListener {
            mOnItemClickListener?.onItemLongClick(holder.itemView, holder.absoluteAdapterPosition)
            true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun setSelection(position: Int) {
        if (selectedPos == position) return
        notifyItemChanged(selectedPos)
        this.selectedPos = position
        notifyItemChanged(selectedPos)
    }

    fun setNewData(newData: List<String>?) {
        dataLists.clear()
        if (newData != null && newData.isNotEmpty()) {
            dataLists.addAll(newData)
        }
        notifyDataSetChanged()
    }

    fun addData(datas: List<String>?) {
        if (datas == null || datas.isEmpty()) {
            return
        }
        val pos = dataLists.size
        dataLists.addAll(datas)
        notifyItemRangeInserted(pos, dataLists.size)
    }

    fun clearData() {
        dataLists.clear()
        notifyDataSetChanged()
    }


    inner class MyViewHolder(itemView: LayoutTabIndicatorBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onItemLongClick(view: View?, position: Int)
    }

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mOnItemClickListener
    }
}