package com.example.kotlinjetpack.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemBehaviorListBinding

class BehaviorRCVAdapter(val datas: ArrayList<String>) :
    RecyclerView.Adapter<BehaviorRCVAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: ItemBehaviorListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemBehaviorListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvTitle.text = datas[position]
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun getDataList() = datas
}