package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.msg.*
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface MsgApi {

    @GET("${SUNNY_BEACH_BASE_URL}ct/msg/read")
    suspend fun readAllMsg(): ApiResponse<Any>

    @GET("${SUNNY_BEACH_BASE_URL}ct/ucenter/message/article/{page}")
    suspend fun getArticleMsgList(@Path("page") page: Int): ApiResponse<ArticleMsg>

    @GET("${SUNNY_BEACH_BASE_URL}ct/ucenter/message/moment/{page}")
    suspend fun getMomentMsgList(@Path("page") page: Int): ApiResponse<MomentMsg>

    @GET("${SUNNY_BEACH_BASE_URL}ct/ucenter/message/wenda/{page}")
    suspend fun getQAMsgList(@Path("page") page: Int): ApiResponse<QAMsg>

    @GET("${SUNNY_BEACH_BASE_URL}ct/ucenter/message/thumb/{page}")
    suspend fun getLikeMsgList(@Path("page") page: Int): ApiResponse<LikeMsg>

    @GET("${SUNNY_BEACH_BASE_URL}ct/ucenter/message/system/{page}")
    suspend fun getSystemMsgList(@Path("page") page: Int): ApiResponse<SystemMsg>

    @GET("${SUNNY_BEACH_BASE_URL}ct/ucenter/message/at/{page}")
    suspend fun getAtMeMsgList(@Path("page") page: Int): ApiResponse<AtMeMsg>
}