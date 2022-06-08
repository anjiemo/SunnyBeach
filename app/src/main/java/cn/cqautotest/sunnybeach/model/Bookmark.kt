package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 收藏夹
 */
data class Bookmark(
    @SerializedName("content")
    val content: List<Content>,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int
) {
    data class Content(
        @SerializedName("avatar")
        val avatar: Any,
        @SerializedName("cover")
        val cover: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("favoriteCount")
        val favoriteCount: Int,
        @SerializedName("followCount")
        val followCount: Int,
        @SerializedName("_id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("permission")
        val permission: String,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("userName")
        val userName: Any
    )

    data class Pageable(
        @SerializedName("offset")
        val offset: Int,
        @SerializedName("pageNumber")
        val pageNumber: Int,
        @SerializedName("pageSize")
        val pageSize: Int,
        @SerializedName("paged")
        val paged: Boolean,
        @SerializedName("sort")
        val sort: Sort,
        @SerializedName("unpaged")
        val unpaged: Boolean
    ) {
        data class Sort(
            @SerializedName("sorted")
            val sorted: Boolean,
            @SerializedName("unsorted")
            val unsorted: Boolean
        )
    }

    data class Sort(
        @SerializedName("sorted")
        val sorted: Boolean,
        @SerializedName("unsorted")
        val unsorted: Boolean
    )
}