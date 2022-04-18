package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 问答获取
 */
object QaNetwork : INetworkApi {

    suspend fun loadUserQaList(userId: String, page: Int) =
        qaApi.loadUserQaList(userId, page)
}