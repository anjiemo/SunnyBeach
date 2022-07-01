package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.sob.ShareApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 分享获取
 */
object ShareNetwork {

    suspend fun loadUserShareList(userId: String, page: Int) =
        ShareApi.loadUserShareList(userId, page)
}