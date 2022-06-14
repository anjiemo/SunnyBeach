package cn.cqautotest.sunnybeach.http.network

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/01
 * desc   : App信息获取
 */
object AppNetwork : INetworkApi {

    suspend fun checkAppUpdate() = appApi.checkAppUpdate()

    suspend fun getMourningCalendar() = appApi.getMourningCalendar()
}