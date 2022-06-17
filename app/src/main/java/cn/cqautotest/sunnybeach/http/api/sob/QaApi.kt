package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.annotation.SobClient
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserQa
import retrofit2.http.GET
import retrofit2.http.Path

@SobClient
interface QaApi {

    /**
     * 获取指定用户的回答列表
     */
    @GET("ct/wenda/comment/list/user/{userId}/{page}")
    suspend fun loadUserQaList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserQa>

    companion object : QaApi by ServiceCreator.create()
}