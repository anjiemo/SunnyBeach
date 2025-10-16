package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.ArticleSearchFilter
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.paging.source.ArticlePagingSource
import cn.cqautotest.sunnybeach.paging.source.UserArticlePagingSource
import cn.cqautotest.sunnybeach.paging.source.content.UserPublishArticlePagingSource
import cn.cqautotest.sunnybeach.repository.Repository
import cn.cqautotest.sunnybeach.ui.fragment.user.content.UserArticleListManagerFragment

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/27
 * desc   : 文章 ViewModel
 */
class ArticleViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val userArticleListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val userId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            UserArticlePagingSource(userId)
        }).flow.cachedIn(viewModelScope)

    val articleListFlow = Pager(
        config = PagingConfig(30),
        pagingSourceFactory = {
            val categoryId = savedStateHandle.get<String>(IntentKey.ID).orEmpty()
            ArticlePagingSource(categoryId)
        }).flow.cachedIn(viewModelScope)

    val searchUserArticleListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            val searchFilter = savedStateHandle.get<ArticleSearchFilter>(UserArticleListManagerFragment.SEARCH_FILTER) ?: ArticleSearchFilter()
            UserPublishArticlePagingSource(searchFilter)
        }).flow.cachedIn(viewModelScope)

    fun getArticleDetailById(articleId: String) = Repository.getArticleDetailById(articleId)

    fun articleLikes(articleId: String) = Repository.articleLikes(articleId)
}