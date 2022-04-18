package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 关注获取
 */
object FollowNetwork : INetworkApi {

    suspend fun loadUserFollowList(userId: String, page: Int) = followApi.loadUserFollowList(userId, page)
}