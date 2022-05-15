package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 搜索结果
 */
class SearchResult : Page<SearchResult.SearchResultItem>() {
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