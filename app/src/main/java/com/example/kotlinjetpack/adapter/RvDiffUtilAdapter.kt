package com.example.kotlinjetpack.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinjetpack.R

/**
 * desc: RvDiffUtilAdapter
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/3/18 16:20
 */
class MyAdapter(private var mOldList: List<String>, private var mNewList: List<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    // ViewHolder，用于缓存列表项的视图
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextView: TextView

        init {
            mTextView = itemView.findViewById(R.id.tv_title)
        }
    }

    // 创建 ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_common_list, parent, false)
        return ViewHolder(view)
    }

    // 绑定 ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTextView.text = mNewList[position]
    }

    // 获取数据集大小
    override fun getItemCount(): Int {
        return mNewList.size
    }

    // 更新数据集
    fun updateList(newList: List<String>) {
        // 计算差异
        val callback: DiffUtil.Callback = MyCallback(mOldList, newList)
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(callback)
//        同 calculateDiff() 方法，但可以指定是否开启移动操作的检测。
//        • public static DiffUtil.DiffResult calculateDiff(DiffUtil.Callback callback, boolean detectMoves)
//        一个更加灵活的差异计算方法，可以自定义对象的比较方式，并且支持异步计算。
//        • public static List diff(List oldList, List newList, ItemCallback callback)
//        异步计算差异的工具类，可以在子线程中进行差异计算，并在主线程中更新 UI
//        • public static class AsyncDiffResult extends AsyncTask<Void, Void, DiffUtil.DiffResult>
//        val asyncTask = object : AsyncTask<Void, Void, DiffUtil.DiffResult>() {
//            override fun doInBackground(vararg voids: Void): DiffUtil.DiffResult {
//                return DiffUtil.calculateDiff(callback)
//            }
//
//            override fun onPostExecute(diffResult: DiffUtil.DiffResult) {
//                messageList = newList
//                diffResult.dispatchUpdatesTo(this@ChatListAdapter)
//            }
//        }
//        asyncTask.execute()
//        在使用异步计算时，我们不能直接调用 notifyDataSetChanged() 方法来刷新列表，而是需要通过 DiffUtil.DiffResult
//        的 dispatchUpdatesTo() 方法来更新列表。

        // 更新数据集
        mOldList = mNewList
        mNewList = newList
        result.dispatchUpdatesTo(this)
    }

    // 自定义的 DiffUtil.Callback
    private inner class MyCallback(
        private val mOldList: List<String>, private val mNewList: List<String>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return mOldList.size
        }

        override fun getNewListSize(): Int {
            return mNewList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition] == mNewList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return mOldList[oldItemPosition] == mNewList[newItemPosition]
        }

    }
}