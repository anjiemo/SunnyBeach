package cn.cqautotest.sunnybeach.action

import android.os.Bundle

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/15
 * desc   : 评论意图
 */
interface CommendAction {

    /**
     * 获取包装了评论的参数
     */
    fun getCommentArgs(): Bundle

    /**
     * 获取被评论的用户名称
     */
    fun getTargetUserName() = getCommentArgs().getString(TARGET_USER_NAME).orEmpty()

    /**
     * 获取动态Id
     */
    fun getMomentId() = getCommentArgs().getString(MOMENT_ID).orEmpty()

    /**
     * 获取被评论内容的Id
     */
    fun getCommentId() = getCommentArgs().getString(COMMENT_ID)

    /**
     * 被评论内容的用户Id
     */
    fun getTargetUserId() = getCommentArgs().getString(TARGET_USER_ID)

    /**
     * 是否为评论回复
     * 回复：true
     * 评论：false
     */
    fun isReply() = getCommentArgs().getBoolean(IS_REPLY, false)

    companion object {

        // 动态Id
        const val MOMENT_ID = "moment_id"

        // 被评论内容的Id
        const val COMMENT_ID = "comment_id"

        // 被评论内容的用户Id
        const val TARGET_USER_ID = "target_user_id"

        // 被评论内容的用户名称
        const val TARGET_USER_NAME = "target_user_name"

        // 是否为评论回复
        const val IS_REPLY = "is_reply"
    }
}