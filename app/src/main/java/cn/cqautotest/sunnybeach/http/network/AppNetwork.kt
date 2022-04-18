package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.util.APP_INFO_URL
import cn.cqautotest.sunnybeach.util.MOURNING_CALENDAR_URL

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/01
 * desc   : App信息获取
 */
object AppNetwork : INetworkApi {

    suspend fun checkAppUpdate(url: String = APP_INFO_URL) = appApi.checkAppUpdate(url)

    suspend fun getMourningCalendar(url: String = MOURNING_CALENDAR_URL) = appApi.getMourningCalendar(url)
}