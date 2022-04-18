package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/10/24
 *    desc   : 消息操作
 */
object MsgNetwork : INetworkApi {

    suspend fun readQaMsg(msgId: String) = msgApi.readQaMsg(msgId)

    suspend fun readAtMeMsg(msgId: String) = msgApi.readAtMeMsg(msgId)

    suspend fun readMomentMsg(msgId: String) = msgApi.readMomentMsg(msgId)

    suspend fun readArticleMsg(msgId: String, state: Int) = msgApi.readArticleMsg(msgId, state)

    suspend fun readAllMsg() = msgApi.readAllMsg()

    suspend fun getUnReadMsgCount() = msgApi.getUnReadMsgCount()

    suspend fun getArticleMsgList(page: Int) = msgApi.getArticleMsgList(page)

    suspend fun getMomentMsgList(page: Int) = msgApi.getMomentMsgList(page)

    suspend fun getQAMsgList(page: Int) = msgApi.getQaMsgList(page)

    suspend fun getLikeMsgList(page: Int) = msgApi.getLikeMsgList(page)

    suspend fun getSystemMsgList(page: Int) = msgApi.getSystemMsgList(page)
}