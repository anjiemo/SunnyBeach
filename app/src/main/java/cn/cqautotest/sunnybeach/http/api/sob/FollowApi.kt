package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserFollow
import retrofit2.http.GET
import retrofit2.http.Path

interface FollowApi : ISobApi {

    /**
     * 获取用户关注的用户列表
     */
    @GET("uc/follow/list/{userId}/{page}")
    suspend fun loadUserFollowList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>
}