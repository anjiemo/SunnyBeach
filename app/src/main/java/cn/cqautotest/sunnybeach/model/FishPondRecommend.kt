package cn.cqautotest.sunnybeach.model
import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/7
 * desc   : 摸鱼评论列表bean类
 */
data class FishPondRecommend(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPre")
    val hasPre: Boolean,
    @SerializedName("list")
    val list: List<FishPondRecommendItem>,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) {
    data class FishPondRecommendItem(
        @SerializedName("avatar")
        val avatar: String,
        @SerializedName("company")
        val company: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("momentId")
        val momentId: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("position")
        val position: String,
        @SerializedName("subComments")
        val subComments: List<SubComment>,
        @SerializedName("thumbUp")
        val thumbUp: Int,
        @SerializedName("thumbUpList")
        val thumbUpList: List<Any>,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("vip")
        val vip: Boolean
    ) {
        data class SubComment(
            @SerializedName("avatar")
            val avatar: String,
            @SerializedName("commentId")
            val commentId: String,
            @SerializedName("company")
            val company: String,
            @SerializedName("content")
            val content: String,
            @SerializedName("createTime")
            val createTime: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("nickname")
            val nickname: String,
            @SerializedName("position")
            val position: String,
            @SerializedName("targetUserId")
            val targetUserId: Any,
            @SerializedName("targetUserIsVip")
            val targetUserIsVip: Boolean,
            @SerializedName("targetUserNickname")
            val targetUserNickname: String,
            @SerializedName("thumbUpList")
            val thumbUpList: List<Any>,
            @SerializedName("userId")
            val userId: String,
            @SerializedName("vip")
            val vip: Boolean
        )
    }
}