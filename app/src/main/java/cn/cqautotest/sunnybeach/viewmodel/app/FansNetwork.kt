package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.FansApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 粉丝获取
 */
object FansNetwork {

    private val fansApi = ServiceCreator.create<FansApi>()

    suspend fun loadUserFansList(userId: String, page: Int) =
        fansApi.loadUserFansList(userId, page)
}