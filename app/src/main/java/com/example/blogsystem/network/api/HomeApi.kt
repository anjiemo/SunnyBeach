package com.example.blogsystem.network.api

import com.example.blogsystem.model.ArticleInfo
import com.example.blogsystem.model.BaseResponse
import com.example.blogsystem.model.HomeCategories
import com.example.blogsystem.utils.BLOG_BASE_URL
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {

    @GET("${BLOG_BASE_URL}ct/content/home/recommend/{page}")
    suspend fun getRecommendContent(@Path("page") page: Int): BaseResponse<ArticleInfo>

    @GET("${BLOG_BASE_URL}ct/category/list")
    suspend fun getCategories(): BaseResponse<HomeCategories>
}