package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.FollowApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 关注获取
 */
object FollowNetwork {

    private val followApi = ServiceCreator.create<FollowApi>()

    suspend fun loadUserFollowList(userId: String, page: Int) =
        followApi.loadUserFollowList(userId, page)
}