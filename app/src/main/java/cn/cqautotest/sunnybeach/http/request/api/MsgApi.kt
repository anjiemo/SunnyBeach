package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.msg.*
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface MsgApi {

    /**
     * 已读所有消息
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/msg/read")
    suspend fun readAllMsg(): ApiResponse<Any>

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