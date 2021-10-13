package cn.cqautotest.sunnybeach.model

interface UserComment {

    fun getId(): String

    fun getCommentId(): String

    fun getNickName(): String

    fun getUserId(): String

    fun getTargetUserId(): String

    fun getTargetUserNickname(): String
}