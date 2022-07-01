package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.paging.source.ArticlePagingSource
import cn.cqautotest.sunnybeach.paging.source.UserArticlePagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/27
 * desc   : 文章 ViewModel
 */
class ArticleViewModel : ViewModel() {

    fun getArticleDetailById(articleId: String) = Repository.getArticleDetailById(articleId)

    fun getUserArticleList(userId: String): Flow<PagingData<UserArticle.UserArticleItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserArticlePagingSource(userId)
            }).flow.cachedIn(viewModelScope)
    }

    fun getArticleListByCategoryId(categoryId: String): Flow<PagingData<ArticleInfo.ArticleItem>> {
        return Pager(
            config = PagingConfig(30),
            pagingSourceFactory = {
                ArticlePagingSource(categoryId)
            }).flow.cachedIn(viewModelScope)
    }
}