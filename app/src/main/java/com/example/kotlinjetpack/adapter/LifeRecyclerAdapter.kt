package com.example.kotlinjetpack.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.model.LifeItem

/**
 * desc: LifeRecyclerAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/21 15:22
 */
class LifeRecyclerAdapter(val context: Context, val itemArray: ArrayList<LifeItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    // 创建列表项的视图持有者
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 根据布局文件item_life.xml生成视图对象
        val v: View = LayoutInflater.from(context).inflate(R.layout.item_life, parent, false)
        return ItemHolder(v)
    }

    // 绑定列表项的视图持有者
    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        val holder = vh as ItemHolder
        holder.iv_pic.setImageResource(itemArray[position].pic)
        holder.tv_title.setText(itemArray[position].title)
    }

    // 获取列表项的个数
    override fun getItemCount(): Int {
        return itemArray.size
    }

    // 获取列表项的类型
    override fun getItemViewType(position: Int): Int {
        return 0
    }

    // 获取列表项的编号
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // 定义列表项的视图持有者
    inner class ItemHolder(v: View) : RecyclerView.ViewHolder(v) {
        var iv_pic // 声明列表项图标的图像视图
                : ImageView
        var tv_title // 声明列表项标题的文本视图
                : TextView

        init {
            iv_pic = v.findViewById(R.id.iv_pic_head)
            tv_title = v.findViewById(R.id.tv_title)
        }
    }
}