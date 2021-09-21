package cn.cqautotest.sunnybeach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.paging.source.ArticlePagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/9/27
 * desc   : 文章 ViewModel
 */
class ArticleViewModel : ViewModel() {

    fun getArticleListByCategoryId(categoryId: String): Flow<PagingData<ArticleInfo.ArticleItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                ArticlePagingSource(categoryId)
            }).flow.cachedIn(viewModelScope)
    }
}