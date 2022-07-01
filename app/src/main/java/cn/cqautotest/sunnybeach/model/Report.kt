package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * 举报
 * type：1、文章，2、问答，3、动态，4、分享，5、文章评论，6、问答评论，7、动态评论，8、用户
 * contentId：内容 id
 * url：链接，如果是问答的举报，链接是问答的url地址，如果是问答评论的举报，链接还是问答地址
 * why：举报理由
 */
data class Report(
    @SerializedName("type")
    private val type: Int,
    @SerializedName("contentId")
    private val contentId: String,
    @SerializedName("url")
    private val url: String,
    @SerializedName("why")
    private val why: String
)