package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.UserApi

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 用户信息获取
 */
object UserNetwork {

    private val userApi = ServiceCreator.create<UserApi>()

    suspend fun getRichList(count: Int = 30) = userApi.getRichList(count)
}