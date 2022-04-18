package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.UserArticle
import retrofit2.http.GET
import retrofit2.http.Path

interface ArticleApi : ISobApi {

    /**
     * 获取指定用户的文章列表
     */
    @GET("ct/article/list/{userId}/{page}")
    suspend fun loadUserArticleList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserArticle>
}