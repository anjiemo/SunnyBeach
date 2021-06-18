package com.example.blogsystem.model

import com.google.gson.annotations.SerializedName

/**
 * 首页分类
 */
class HomeCategories : ArrayList<HomeCategories.HomeCategoriesItem>() {
    data class HomeCategoriesItem(
        @SerializedName("categoryName")
        val categoryName: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("order")
        val order: Int,
        @SerializedName("pyName")
        val pyName: String,
        @SerializedName("updateTime")
        val updateTime: Any
    )
}