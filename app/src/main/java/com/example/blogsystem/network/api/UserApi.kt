package com.example.blogsystem.network.api

import com.example.blogsystem.model.BaseResponse
import com.example.blogsystem.model.User
import com.example.blogsystem.model.UserBasicInfo
import com.example.blogsystem.utils.BLOG_BASE_URL
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @POST("${BLOG_BASE_URL}uc/user/login/{captcha}")
    suspend fun login(@Path("captcha") captcha: String, @Body user: User): BaseResponse<String>

    @GET("${BLOG_BASE_URL}uc/user/checkToken")
    suspend fun checkToken(): BaseResponse<UserBasicInfo>
}