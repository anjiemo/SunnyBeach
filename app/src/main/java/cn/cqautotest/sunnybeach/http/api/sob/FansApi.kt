package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserFollow
import retrofit2.http.GET
import retrofit2.http.Path

interface FansApi : ISobApi {

    /**
     * 获取用户的粉丝列表
     */
    @GET("uc/fans/list/{userId}/{page}")
    suspend fun loadUserFansList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>
}