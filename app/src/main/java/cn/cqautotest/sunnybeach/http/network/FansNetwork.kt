package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 粉丝获取
 */
object FansNetwork : INetworkApi {

    suspend fun loadUserFansList(userId: String, page: Int) = fansApi.loadUserFansList(userId, page)
}