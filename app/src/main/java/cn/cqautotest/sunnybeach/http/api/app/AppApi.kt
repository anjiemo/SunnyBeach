package cn.cqautotest.sunnybeach.http.api.app

import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.model.MourningCalendar
import retrofit2.http.GET
import retrofit2.http.Url

interface AppApi {

    /**
     * 检查 App 更新
     */
    @GET
    suspend fun checkAppUpdate(@Url url: String): ApiResponse<AppUpdateInfo>

    /**
     * 获取哀悼日历
     */
    @GET
    suspend fun getMourningCalendar(@Url url: String): ApiResponse<List<MourningCalendar>>
}