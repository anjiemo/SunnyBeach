package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.sob.FansApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 粉丝获取
 */
object FansNetwork {

    suspend fun loadUserFansList(userId: String, page: Int) = FansApi.loadUserFansList(userId, page)
}