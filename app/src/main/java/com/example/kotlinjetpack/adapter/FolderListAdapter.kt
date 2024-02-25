package com.example.kotlinjetpack.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemPicListBinding
import com.example.kotlinjetpack.utils.pic.FrescoImageLoader
import com.luck.picture.lib.entity.LocalMediaFolder

/**
 * desc: FolderListAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/8 10:03 上午
 */
class FolderListAdapter(val datas: MutableList<LocalMediaFolder>) :
    RecyclerView.Adapter<FolderListAdapter.MyViewHolder>() {


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
        Log.e( "onBindViewHolder: ", "图片地址：${datas[position].folderTotalNum}")
        Log.e( "onBindViewHolder: ", "图片地2：${datas[position].folderName}")
        Log.e( "onBindViewHolder: ", "图片地2：${datas[position].firstImagePath}")
        println( "图片地2：${datas[position].bucketId}")
        println( "图片地2：${datas[position].currentDataPage}")
        println( "图片地2：${datas[position].data.size}")
        println( "图片地2：${datas[position].firstMimeType}")
        println( "图片地2：${datas[position].isHasMore}")
        println( "图片地2：${datas[position].isSelectTag}")
        FrescoImageLoader.loadFile(holder.binding.sdvImgThumbnail,datas[position].firstImagePath)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun refreshData(newDatas: MutableList<LocalMediaFolder>) {
        this.datas.clear()
        this.datas.addAll(newDatas)
        notifyDataSetChanged()
    }

}