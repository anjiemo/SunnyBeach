package com.example.blogsystem.model

import com.google.gson.annotations.SerializedName

/**
 * 推荐
 */
data class ArticleInfo(
    @SerializedName("currentPage")
    val currentPage: Int = 0,
    @SerializedName("hasNext")
    val hasNext: Boolean = true,
    @SerializedName("hasPre")
    val hasPre: Boolean = false,
    @SerializedName("list")
    val list: MutableList<ArticleItem> = arrayListOf(),
    @SerializedName("pageSize")
    val pageSize: Int = 1,
    @SerializedName("total")
    val total: Int = 20,
    @SerializedName("totalPage")
    val totalPage: Int = 1
) {
    data class ArticleItem(
        @SerializedName("avatar")
        val avatar: String = "",
        @SerializedName("covers")
        val covers: List<String> = listOf(),
        @SerializedName("createTime")
        val createTime: String = "",
        @SerializedName("id")
        val id: String = "",
        @SerializedName("nickName")
        val nickName: String = "",
        @SerializedName("thumbUp")
        val thumbUp: Int = 0,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type")
        val type: Int = 0,
        @SerializedName("userId")
        val userId: String = "",
        @SerializedName("viewCount")
        val viewCount: Int = 0,
        @SerializedName("vip")
        val vip: Boolean = false
    )
}