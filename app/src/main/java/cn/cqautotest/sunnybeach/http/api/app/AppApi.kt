package cn.cqautotest.sunnybeach.http.api.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.annotation.baseurl.GiteeBaseUrl
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.model.MourningCalendar
import retrofit2.http.GET

@GiteeBaseUrl
interface AppApi {

    /**
     * 检查 App 更新
     */
    @GET("appconfig.json")
    suspend fun checkAppUpdate(): ApiResponse<AppUpdateInfo>

    /**
     * 获取哀悼日历
     */
    @GET("mourning_calendar.json")
    suspend fun getMourningCalendar(): ApiResponse<List<MourningCalendar>>

    companion object : AppApi by ServiceCreator.create()
}
