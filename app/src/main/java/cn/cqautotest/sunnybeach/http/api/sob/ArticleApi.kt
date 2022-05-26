package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.http.annotation.SobClient
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.ArticleDetail
import cn.cqautotest.sunnybeach.model.UserArticle
import retrofit2.http.GET
import retrofit2.http.Path

@SobClient
interface ArticleApi {

    /**
     * 根据文章id获取文章详情内容
     */
    @GET("ct/article/detail/{articleId}")
    suspend fun getArticleDetailById(@Path("articleId") articleId: String): ApiResponse<ArticleDetail>

    /**
     * 获取指定用户的文章列表
     */
    @GET("ct/article/list/{userId}/{page}")
    suspend fun loadUserArticleList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserArticle>
}