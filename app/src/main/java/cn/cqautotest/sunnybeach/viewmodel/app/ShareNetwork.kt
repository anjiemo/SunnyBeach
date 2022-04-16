package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.ShareApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 分享获取
 */
object ShareNetwork {

    private val shareApi = ServiceCreator.create<ShareApi>()

    suspend fun loadUserShareList(userId: String, page: Int) =
        shareApi.loadUserShareList(userId, page)
}