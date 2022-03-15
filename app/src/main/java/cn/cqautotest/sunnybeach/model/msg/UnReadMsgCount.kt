package cn.cqautotest.sunnybeach.model.msg

import com.google.gson.annotations.SerializedName

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/14
 * desc   : 未读消息
 */
data class UnReadMsgCount(
    @SerializedName("articleMsgCount")
    val articleMsgCount: Int = 0,
    @SerializedName("atMsgCount")
    val atMsgCount: Int = 0,
    @SerializedName("momentCommentCount")
    val momentCommentCount: Int = 0,
    @SerializedName("shareMsgCount")
    val shareMsgCount: Int = 0,
    @SerializedName("systemMsgCount")
    val systemMsgCount: Int = 0,
    @SerializedName("thumbUpMsgCount")
    val thumbUpMsgCount: Int = 0,
    @SerializedName("wendaMsgCount")
    val wendaMsgCount: Int = 0
) {

    val hasUnReadMsg: Boolean
        get() = articleMsgCount > 0 || atMsgCount > 0 || momentCommentCount > 0 ||
                shareMsgCount > 0 || systemMsgCount > 0 || thumbUpMsgCount > 0 || wendaMsgCount > 0
}