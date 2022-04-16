package cn.cqautotest.sunnybeach.http.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserQa
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface QaApi {

    /**
     * 获取指定用户的回答列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/wenda/comment/list/user/{userId}/{page}")
    suspend fun loadUserQaList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserQa>
}