package com.example.blogsystem.network.api

import com.example.blogsystem.model.AppUpdateInfo
import com.example.blogsystem.model.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface AppApi {

    @GET
    suspend fun checkAppUpdate(@Url url: String): BaseResponse<AppUpdateInfo>
}