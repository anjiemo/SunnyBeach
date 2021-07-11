package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.ArticleDetail
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.model.BaseResponse
import cn.cqautotest.sunnybeach.model.HomeCategories
import cn.cqautotest.sunnybeach.utils.SUNNY_BEACH_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {

    /**
     * 根据文章id获取文章详情内容
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/article/detail/md/{articleId}")
    suspend fun getArticleDetailById(@Path("articleId") articleId: String): BaseResponse<ArticleDetail>

    /**
     * 根据分类id获取内容
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/content/home/recommend/{categoryId}/{page}")
    suspend fun getArticleListByCategoryId(
        @Path("categoryId") categoryId: String,
        @Path("page") page: Int
    ): BaseResponse<ArticleInfo>

    /**
     * 获取推荐内容
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/content/home/recommend/{page}")
    suspend fun getRecommendContent(@Path("page") page: Int): BaseResponse<ArticleInfo>

    /**
     * 获取分类列表
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/category/list")
    suspend fun getCategories(): BaseResponse<HomeCategories>
}