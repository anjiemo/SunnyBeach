package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.sob.ArticleApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/10/31
 *    desc   : 文章获取
 */
object ArticleNetwork {

    suspend fun getArticleDetailById(articleId: String) = ArticleApi.getArticleDetailById(articleId)

    suspend fun loadUserArticleList(userId: String, page: Int) = ArticleApi.loadUserArticleList(userId, page)

    suspend fun articleLikes(articleId: String) = ArticleApi.articleLikes(articleId)
}