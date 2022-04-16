package cn.cqautotest.sunnybeach.http.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserFollow
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface FollowApi {

    /**
     * 获取用户关注的用户列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/follow/list/{userId}/{page}")
    suspend fun loadUserFollowList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>
}