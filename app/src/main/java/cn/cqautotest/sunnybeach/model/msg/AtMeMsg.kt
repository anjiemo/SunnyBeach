package cn.cqautotest.sunnybeach.model.msg

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/26
 * desc   : @我 消息
 */
data class AtMeMsg(
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
        val avatar: String,
        @SerializedName("beNickname")
        val beNickname: Any?,
        @SerializedName("beUid")
        val beUid: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("exId")
        val exId: String,
        @SerializedName("hasRead")
        val hasRead: String,
        @SerializedName("_id")
        val id: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("publishTime")
        val publishTime: String,
        @SerializedName("timeText")
        val timeText: Any?,
        @SerializedName("type")
        val type: String,
        @SerializedName("uid")
        val uid: String,
        @SerializedName("url")
        val url: String
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