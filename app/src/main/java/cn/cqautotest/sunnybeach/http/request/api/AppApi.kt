package cn.cqautotest.sunnybeach.http.request.api

import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.model.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface AppApi {

    @GET
    suspend fun checkAppUpdate(@Url url: String): BaseResponse<AppUpdateInfo>
}