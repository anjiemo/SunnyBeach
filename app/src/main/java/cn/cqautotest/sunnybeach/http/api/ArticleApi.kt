package cn.cqautotest.sunnybeach.http.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleApi {

    /**
     * 获取指定用户的文章列表
     */
    @GET("${SUNNY_BEACH_API_BASE_URL}ct/article/list/{userId}/{page}")
    suspend fun loadUserArticleList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserArticle>
}