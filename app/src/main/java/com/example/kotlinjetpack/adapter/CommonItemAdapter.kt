package com.example.kotlinjetpack.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemCommonListBinding
import kotlin.random.Random

class CommonItemAdapter(val datas: ArrayList<String>) :
    RecyclerView.Adapter<CommonItemAdapter.MyViewHolder>() {

    private val colorList by lazy {
        arrayListOf<Int>().apply {
            add(Color.RED)
            add(Color.GREEN)
            add(Color.BLUE)
            add(Color.GRAY)
            add(Color.DKGRAY)
            add(Color.LTGRAY)
            add(Color.CYAN)
            add(Color.YELLOW)
            add(Color.MAGENTA)
        }
    }

    inner class MyViewHolder(itemView: ItemCommonListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemCommonListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvTitle.text = datas[position]
        holder.binding.tvTitle.setBackgroundColor(colorList[Random.nextInt(colorList.size)])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun getDataList() = datas
}