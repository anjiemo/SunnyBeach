package com.example.blogsystem.network.api

import com.example.blogsystem.model.ArticleInfo
import com.example.blogsystem.model.BaseResponse
import com.example.blogsystem.model.HomeCategories
import com.example.blogsystem.utils.SUNNY_BEACH_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {

    /**
     * 根据分类id获取内容
     */
    @GET("${SUNNY_BEACH_BASE_URL}ct/content/home/recommend/{categoryId}/{page}")
    suspend fun getCommendContentByCategoryId(
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