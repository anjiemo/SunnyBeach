package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.ArticleDetail
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.model.HomeCategories
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi : ISobApi {

    /**
     * 根据文章id获取文章详情内容
     */
    @GET("ct/article/detail/{articleId}")
    suspend fun getArticleDetailById(@Path("articleId") articleId: String): ApiResponse<ArticleDetail>

    /**
     * 根据分类id获取内容
     */
    @GET("ct/content/home/recommend/{categoryId}/{page}")
    suspend fun getArticleListByCategoryId(
        @Path("categoryId") categoryId: String,
        @Path("page") page: Int
    ): ApiResponse<ArticleInfo>

    /**
     * 获取推荐内容
     */
    @GET("ct/content/home/recommend/{page}")
    suspend fun getRecommendContent(@Path("page") page: Int): ApiResponse<ArticleInfo>

    /**
     * 获取分类列表
     */
    @GET("ct/category/list")
    suspend fun getCategories(): ApiResponse<HomeCategories>
}