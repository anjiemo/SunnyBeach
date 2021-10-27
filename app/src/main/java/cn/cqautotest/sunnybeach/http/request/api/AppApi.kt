package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import retrofit2.http.GET
import retrofit2.http.Url

interface AppApi {

    @GET
    suspend fun checkAppUpdate(@Url url: String): ApiResponse<AppUpdateInfo>
}