package com.example.kotlinjetpack.model

import com.example.kotlinjetpack.R

/**
 * desc: LifeItem
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/21 15:26
 */
data class LifeItem(val pic: Int, val title: String) {
    fun getDefaultData(): ArrayList<LifeItem> {
        val itemArray = ArrayList<LifeItem>()
        for (i in 0..100) {
            itemArray.add(LifeItem(R.drawable.ic_transfer_black_78, "转账"))
        }
        return itemArray
    }
}
