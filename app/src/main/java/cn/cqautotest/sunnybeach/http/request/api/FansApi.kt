package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserFollow
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface FansApi {

    /**
     * 获取用户的粉丝列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}uc/fans/list/{userId}/{page}")
    suspend fun loadUserFansList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>
}