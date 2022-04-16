package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.api.QaApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 问答获取
 */
object QaNetwork {

    private val qaApi = ServiceCreator.create<QaApi>()

    suspend fun loadUserQaList(userId: String, page: Int) =
        qaApi.loadUserQaList(userId, page)
}