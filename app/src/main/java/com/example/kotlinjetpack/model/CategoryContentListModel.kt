package com.example.kotlinjetpack.model

/**
 * desc: CategoryContentListModel
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/5 22:17
 */
data class CategoryContentListModel(
    val categoryOneArray: List<CategoryOneArray>
) : java.io.Serializable

data class CategoryOneArray(
    val cacode: String,
    val categoryTwoArray: List<CategoryTwoArray>,
    val imgsrc: String,
    val name: String
) : java.io.Serializable

data class CategoryTwoArray(
    val cacode: String, val imgsrc: String, val name: String
) : java.io.Serializable