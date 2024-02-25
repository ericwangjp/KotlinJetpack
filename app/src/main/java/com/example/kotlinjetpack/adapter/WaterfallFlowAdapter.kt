package com.example.kotlinjetpack.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.net.Uri
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.databinding.ItemWaterfallFlowBinding
import com.example.kotlinjetpack.model.WaterfallFlowItem
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.imagepipeline.image.ImageInfo


class WaterfallFlowAdapter(
    private val datas: ArrayList<WaterfallFlowItem>,
    private val gestureDetector: GestureDetector
) :
    RecyclerView.Adapter<WaterfallFlowAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: ItemWaterfallFlowBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val binding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemWaterfallFlowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.sdvImg.setOnTouchListener { view, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
            true
        }
        holder.binding.sdvImg.setImageURI(datas[position].resId)
        holder.binding.tvDesc.text = datas[position].desc

        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setOldController(holder.binding.sdvImg.controller)
            .setControllerListener(object : ControllerListener<ImageInfo> {
                override fun onSubmit(id: String?, callerContext: Any?) {

                }

                override fun onFinalImageSet(
                    id: String?,
                    imageInfo: ImageInfo?,
                    animatable: Animatable?
                ) {
                    imageInfo?.let {
                        val params = holder.binding.sdvImg.layoutParams
//                    params.width = screenWidth
                        val scale = imageInfo.width * 1f / holder.binding.sdvImg.width
                        params.height = (imageInfo.height / scale).toInt()
                        holder.binding.sdvImg.layoutParams = params
                    }
                }

                override fun onIntermediateImageSet(id: String?, imageInfo: ImageInfo?) {

                }

                override fun onIntermediateImageFailed(id: String?, throwable: Throwable?) {

                }

                override fun onFailure(id: String?, throwable: Throwable?) {

                }

                override fun onRelease(id: String?) {

                }

            })
            .setUri(Uri.parse(datas[position].resId))
            .build()
        holder.binding.sdvImg.controller = controller
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    fun getDataList() = datas

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        println("绑定到 RecyclerView")
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        println("与 RecyclerView 解除绑定")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(newData: MutableList<WaterfallFlowItem>) {
        this.datas.clear()
        this.datas.addAll(newData)
        notifyDataSetChanged()
    }

    fun changeData(pos: Int, data: String) {
        this.datas[pos] = this.datas[pos].copy(resId = data)
    }
}