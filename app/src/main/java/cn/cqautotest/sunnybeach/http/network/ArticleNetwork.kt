package cn.cqautotest.sunnybeach.http.network

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/10/31
 *    desc   : 文章获取
 */
object ArticleNetwork : INetworkApi {

    suspend fun getArticleDetailById(articleId: String) = articleApi.getArticleDetailById(articleId)

    suspend fun loadUserArticleList(userId: String, page: Int) = articleApi.loadUserArticleList(userId, page)
}