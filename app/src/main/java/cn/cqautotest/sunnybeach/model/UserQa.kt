package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 用户问答列表
 */
data class UserQa(
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
        @SerializedName("wendaComment")
        val wendaComment: WendaComment,
        @SerializedName("wendaTitle")
        val wendaTitle: String
    ) {
        data class WendaComment(
            @SerializedName("avatar")
            val avatar: String,
            @SerializedName("bestAs")
            val bestAs: String,
            @SerializedName("content")
            val content: String,
            @SerializedName("_id")
            val id: String,
            @SerializedName("nickname")
            val nickname: String,
            @SerializedName("publishTime")
            val publishTime: String,
            @SerializedName("publishTimeText")
            val publishTimeText: Any?,
            @SerializedName("subCommentCount")
            val subCommentCount: Int,
            @SerializedName("thumbUp")
            val thumbUp: Int,
            @SerializedName("thumbUps")
            val thumbUps: List<String>,
            @SerializedName("uid")
            val uid: String,
            @SerializedName("vip")
            val vip: Boolean,
            @SerializedName("wendaId")
            val wendaId: String,
            @SerializedName("wendaSubComments")
            val wendaSubComments: List<Any>
        )
    }

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