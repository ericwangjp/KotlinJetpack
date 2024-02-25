package com.example.kotlinjetpack.model

import androidx.databinding.BaseObservable
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemHover
import com.drake.brv.item.ItemPosition

/**
 * desc: GroupModel
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/4 17:50
 */
data class GroupModel(val id: Int, val title: String) : ItemExpand,ItemHover,ItemPosition {
    // 当前条目是否展开
    override var itemExpand: Boolean = false
        set(value) {
            field = value
//            notifyChange()
        }

    // 同级别分组的索引位置
    override var itemGroupPosition: Int = 0

    // 该变量存储子列表
    override fun getItemSublist(): List<Any?>? = subList


//    override var itemSublist: List<Any?>?
//        get() = subList
//        set(value) {
//            subList = value as List<CommonSimpleModel>
//        }


    private var subList: List<CommonSimpleModel> = mutableListOf(
        CommonSimpleModel(1, "子列表一"),
        CommonSimpleModel(2, "子列表二"),
        CommonSimpleModel(3, "子列表三"),
        CommonSimpleModel(4, "子列表四")
    )
    override var itemHover: Boolean = true
    override var itemPosition: Int = 0



}
