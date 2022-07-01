package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.sob.MsgApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/10/24
 *    desc   : 消息操作
 */
object MsgNetwork {

    suspend fun readQaMsg(msgId: String) = MsgApi.readQaMsg(msgId)

    suspend fun readAtMeMsg(msgId: String) = MsgApi.readAtMeMsg(msgId)

    suspend fun readMomentMsg(msgId: String) = MsgApi.readMomentMsg(msgId)

    suspend fun readArticleMsg(msgId: String, state: Int) = MsgApi.readArticleMsg(msgId, state)

    suspend fun readAllMsg() = MsgApi.readAllMsg()

    suspend fun getUnReadMsgCount() = MsgApi.getUnReadMsgCount()

    suspend fun getArticleMsgList(page: Int) = MsgApi.getArticleMsgList(page)

    suspend fun getMomentMsgList(page: Int) = MsgApi.getMomentMsgList(page)

    suspend fun getQAMsgList(page: Int) = MsgApi.getQaMsgList(page)

    suspend fun getLikeMsgList(page: Int) = MsgApi.getLikeMsgList(page)

    suspend fun getSystemMsgList(page: Int) = MsgApi.getSystemMsgList(page)
}