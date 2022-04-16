package cn.cqautotest.sunnybeach.http.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.msg.*
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MsgApi {

    /**
     * 更新问题回答消息的状态
     * msgId：消息id
     */
    @PUT("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/wenda/state/{msgId}/1")
    suspend fun readQaMsg(@Path("msgId") msgId: String): ApiResponse<Any>

    /**
     * 更新At消息的状态
     * msgId：消息id
     */
    @PUT("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/at/state/{msgId}/1")
    suspend fun readAtMeMsg(@Path("msgId") msgId: String): ApiResponse<Any>

    /**
     * 更新摸鱼动态消息的状态
     * msgId：消息id
     */
    @PUT("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/moment/state/{msgId}/1")
    suspend fun readMomentMsg(@Path("msgId") msgId: String): ApiResponse<Any>

    /**
     * 更新文章消息状态
     * msgId：消息id，文章消息字段（_id）
     * state：1表示已读，2表示已回复
     */
    @PUT("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/state/{msgId}/{state}")
    suspend fun readArticleMsg(
        @Path("msgId") msgId: String,
        @Path("state") state: Int
    ): ApiResponse<Any>

    /**
     * 已读所有消息
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/msg/read")
    suspend fun readAllMsg(): ApiResponse<Any>

    /**
     * 获取未读信息
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/msg/count")
    suspend fun getUnReadMsgCount(): ApiResponse<UnReadMsgCount>

    /**
     * 获取文章评论列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/article/{page}")
    suspend fun getArticleMsgList(@Path("page") page: Int): ApiResponse<ArticleMsg>

    /**
     * 获取动态评论列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/moment/{page}")
    suspend fun getMomentMsgList(@Path("page") page: Int): ApiResponse<MomentMsg>

    /**
     * 获取问题的回答列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/wenda/{page}")
    suspend fun getQaMsgList(@Path("page") page: Int): ApiResponse<QaMsg>

    /**
     * 获取点赞列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/thumb/{page}")
    suspend fun getLikeMsgList(@Path("page") page: Int): ApiResponse<LikeMsg>

    /**
     * 获取系统消息列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/system/{page}")
    suspend fun getSystemMsgList(@Path("page") page: Int): ApiResponse<SystemMsg>

    /**
     * 获取被 @ 的消息列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/ucenter/message/at/{page}")
    suspend fun getAtMeMsgList(@Path("page") page: Int): ApiResponse<AtMeMsg>
}