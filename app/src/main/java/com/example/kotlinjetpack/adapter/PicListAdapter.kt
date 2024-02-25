package com.example.kotlinjetpack.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemPicListBinding
import com.example.kotlinjetpack.utils.pic.FrescoImageLoader
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder

/**
 * desc: PicListAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/8 10:03 上午
 */
class PicListAdapter(val datas: MutableList<LocalMedia>) :
    RecyclerView.Adapter<PicListAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: ItemPicListBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemPicListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.e("onBindViewHolder: ", "path：${datas[position].path}")
        Log.e("onBindViewHolder: ", "bucketId：${datas[position].bucketId}")
        Log.e("onBindViewHolder: ", "isChecked：${datas[position].isChecked}")
        Log.e("onBindViewHolder: ", "fileName：${datas[position].fileName}")
        Log.e("onBindViewHolder: ", "num：${datas[position].num}")
        Log.e("onBindViewHolder: ", "size：${datas[position].size}")
        Log.e("onBindViewHolder: ", "parentFolderName：${datas[position].parentFolderName}")
        Log.e("onBindViewHolder: ", "position：${datas[position].position}")
        Log.e("onBindViewHolder: ", "customData：${datas[position].customData}")
        FrescoImageLoader.loadFile(holder.binding.sdvImgThumbnail, datas[position].availablePath)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun refreshData(newDatas: MutableList<LocalMedia>) {
        this.datas.clear()
        this.datas.addAll(newDatas)
        notifyDataSetChanged()
    }

}