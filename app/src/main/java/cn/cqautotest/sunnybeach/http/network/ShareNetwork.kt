package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 分享获取
 */
object ShareNetwork : INetworkApi {

    suspend fun loadUserShareList(userId: String, page: Int) =
        shareApi.loadUserShareList(userId, page)
}