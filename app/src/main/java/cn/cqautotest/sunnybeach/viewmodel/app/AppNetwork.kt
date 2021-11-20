package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.AppApi
import cn.cqautotest.sunnybeach.util.APP_INFO_URL

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/01
 * desc   : App信息获取
 */
object AppNetwork {

    private val appApi = ServiceCreator.create<AppApi>()

    suspend fun checkAppUpdate(url: String = APP_INFO_URL) = appApi.checkAppUpdate(url)
}