package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserQa
import retrofit2.http.GET
import retrofit2.http.Path

interface QaApi : ISobApi {

    /**
     * 获取指定用户的回答列表
     */
    @GET("ct/wenda/comment/list/user/{userId}/{page}")
    suspend fun loadUserQaList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserQa>
}