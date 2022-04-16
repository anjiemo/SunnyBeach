package cn.cqautotest.sunnybeach.model

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/07
 * desc   : 摸鱼评论列表bean类
 */
data class FishPondComment(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean,
    @SerializedName("hasPre")
    val hasPre: Boolean,
    @SerializedName("list")
    val list: List<FishPondCommentItem>,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) {
    data class FishPondCommentItem(
        @SerializedName("avatar")
        val avatar: String = "",
        @SerializedName("company")
        val company: String = "",
        @SerializedName("content")
        val content: String = "",
        @SerializedName("createTime")
        val createTime: String = "",
        @SerializedName("id")
        private val id: String = "",
        @SerializedName("momentId")
        private val momentId: String = "",
        @SerializedName("nickname")
        private val nickname: String = "",
        @SerializedName("position")
        val position: String? = "",
        @SerializedName("subComments")
        val subComments: List<SubComment> = arrayListOf(),
        @SerializedName("thumbUp")
        val thumbUp: Int = 0,
        @SerializedName("thumbUpList")
        val thumbUpList: List<Any> = arrayListOf(),
        @SerializedName("userId")
        private val userId: String = "",
        @SerializedName("vip")
        val vip: Boolean = false
    ) : UserComment {
        data class SubComment(
            @SerializedName("avatar")
            val avatar: String = "",
            @SerializedName("commentId")
            private val commentId: String = "",
            @SerializedName("company")
            val company: String = "",
            @SerializedName("content")
            val content: String = "",
            @SerializedName("createTime")
            val createTime: String = "",
            @SerializedName("id")
            private val id: String = "",
            @SerializedName("nickname")
            private val nickname: String = "",
            @SerializedName("position")
            val position: String? = "",
            @SerializedName("targetUserId")
            private val targetUserId: String = "",
            @SerializedName("targetUserIsVip")
            val targetUserIsVip: Boolean = false,
            @SerializedName("targetUserNickname")
            private val targetUserNickname: String = "",
            @SerializedName("thumbUpList")
            val thumbUpList: List<Any> = arrayListOf(),
            @SerializedName("userId")
            private val userId: String = "",
            @SerializedName("vip")
            val vip: Boolean = false
        ) : UserComment {

            override fun getId(): String = id

            override fun getCommentId(): String = commentId

            override fun getNickName(): String = nickname

            override fun getUserId(): String = userId

            override fun getTargetUserId(): String = targetUserId

            override fun getTargetUserNickname(): String = targetUserNickname
        }

        override fun getId(): String = id

        override fun getCommentId(): String = id

        override fun getNickName(): String = nickname

        override fun getUserId(): String = userId

        override fun getTargetUserId(): String = userId

        override fun getTargetUserNickname(): String = nickname
    }
}