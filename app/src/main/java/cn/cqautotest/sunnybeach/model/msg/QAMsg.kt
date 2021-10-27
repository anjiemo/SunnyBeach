package cn.cqautotest.sunnybeach.model.msg

import cn.cqautotest.sunnybeach.paging.source.msg.impl.IMsgContent
import cn.cqautotest.sunnybeach.paging.source.msg.impl.IMsgPageData
import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 问答评论消息
 */
data class QAMsg(
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
) : IMsgPageData {
    data class Content(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("bUid")
        val bUid: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("hasRead")
        val hasRead: String,
        @SerializedName("_id")
        val id: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("timeText")
        val timeText: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("uid")
        val uid: String,
        @SerializedName("wendaId")
        val wendaId: String
    ) : IMsgContent

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

    override fun isFirst(): Boolean = first

    override fun isLast(): Boolean = last

    override fun getMsgContentList(): List<IMsgContent> = content
}