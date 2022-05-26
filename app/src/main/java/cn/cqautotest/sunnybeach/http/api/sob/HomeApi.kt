package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.http.annotation.SobClient
import cn.cqautotest.sunnybeach.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@SobClient
interface HomeApi {

    /**
     * keyword：关键字
     * type：a-文章（article），w-问答（wenDa），s-分享（share）
     * sort：0-不排序，1-时间升序，2-时间降序
     */
    @GET("ct/search")
    suspend fun searchByKeywords(
        @Query("keyword") keyword: String,
        @Query("type") type: String,
        @Query("page") page: Int,
        @Query("sort") sort: Int
    ): ApiResponse<SearchResult>

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

    /**
     * 获取问答列表
     * page：页码，从 1 开始
     * state：状态，lastest：最新的，noanswer：等待解决的，hot：热门的
     * category：-2（固定参数）
     */
    @GET("ct/wenda/list")
    suspend fun getQaList(
        @Query("page") page: Int,
        @Query("state") state: String,
        @Query("category") category: Int = -2
    ): ApiResponse<QaInfo>
}