package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.app.AppApi

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/01
 * desc   : App信息获取
 */
object AppNetwork {

    suspend fun checkAppUpdate() = AppApi.checkAppUpdate()

    suspend fun getMourningCalendar() = AppApi.getMourningCalendar()
}