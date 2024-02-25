package com.example.kotlinjetpack.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.CommonRcvItemBinding

/**
 * desc: CommonRcvAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/12/4 2:42 下午
 */
class CommonRcvAdapter(val data: List<String>) :
    RecyclerView.Adapter<CommonRcvAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            CommonRcvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvContent.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class MyViewHolder(itemView: CommonRcvItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }
}