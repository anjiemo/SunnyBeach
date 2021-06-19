package com.example.blogsystem.network.api

import com.example.blogsystem.model.BaseResponse
import com.example.blogsystem.model.User
import com.example.blogsystem.model.UserBasicInfo
import com.example.blogsystem.utils.SUNNY_BEACH_BASE_URL
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @POST("${SUNNY_BEACH_BASE_URL}uc/user/login/{captcha}")
    suspend fun login(@Path("captcha") captcha: String, @Body user: User): BaseResponse<String>

    @GET("${SUNNY_BEACH_BASE_URL}uc/user/checkToken")
    suspend fun checkToken(): BaseResponse<UserBasicInfo>
}