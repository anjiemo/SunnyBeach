package cn.cqautotest.sunnybeach.model
import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/7
 * desc   : 摸鱼的数据bean类
 */
class Fish : ArrayList<Fish.FishItem>(){
    data class FishItem(
        @SerializedName("avatar")
        val avatar: Any,
        @SerializedName("commentCount")
        val commentCount: Int,
        @SerializedName("company")
        val company: Any,
        @SerializedName("content")
        val content: String,
        @SerializedName("createTime")
        val createTime: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("images")
        val images: List<Any>,
        @SerializedName("linkCover")
        val linkCover: String,
        @SerializedName("linkTitle")
        val linkTitle: String,
        @SerializedName("linkUrl")
        val linkUrl: String,
        @SerializedName("nickname")
        val nickname: Any,
        @SerializedName("position")
        val position: Any,
        @SerializedName("thumbUpCount")
        val thumbUpCount: Int,
        @SerializedName("thumbUpList")
        val thumbUpList: List<String>,
        @SerializedName("topicId")
        val topicId: String,
        @SerializedName("topicName")
        val topicName: Any,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("vip")
        val vip: Boolean
    )
}