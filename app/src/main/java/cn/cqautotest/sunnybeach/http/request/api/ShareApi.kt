package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserShare
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface ShareApi {

    /**
     * 获取指定用户的分享列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/share/list/{userId}/{page}")
    suspend fun loadUserShareList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserShare>
}