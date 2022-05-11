package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 搜索结果
 */
data class SearchResult(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPre")
    val hasPre: Boolean,
    @SerializedName("list")
    val list: List<SearchResultItem>,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) {
    data class SearchResultItem(
        @SerializedName("content")
        val content: String,
        @SerializedName("cover")
        val cover: String,
        @SerializedName("deleted")
        val deleted: Boolean,
        @SerializedName("id")
        val id: String,
        @SerializedName("labels")
        val labels: List<String>,
        @SerializedName("publishTime")
        val publishTime: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("type")
        val type: String
    )
}