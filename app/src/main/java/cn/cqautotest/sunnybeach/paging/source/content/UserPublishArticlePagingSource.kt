package cn.cqautotest.sunnybeach.paging.source.content

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.network.UserNetwork
import cn.cqautotest.sunnybeach.model.ArticleSearchFilter
import cn.cqautotest.sunnybeach.model.UserArticle
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/11/27
 * desc   : 当前用户已发布的文章 PagingSource
 */
class UserPublishArticlePagingSource(private val articleSearchFilter: ArticleSearchFilter) :
    PagingSource<Int, UserArticle.UserArticleItem>() {

    override fun getRefreshKey(state: PagingState<Int, UserArticle.UserArticleItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserArticle.UserArticleItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> page is $page articleSearchFilter is $articleSearchFilter")
            val response = UserNetwork.searchArticleList(page = page, articleSearchFilter = articleSearchFilter)
            val responseData = response.getData()
            val currentPage = responseData.currentPage
            val prevKey = if (responseData.hasPre) currentPage - 1 else null
            val nextKey = if (responseData.hasNext) currentPage + 1 else null
            if (response.isSuccess()) LoadResult.Page(
                data = responseData.list,
                prevKey = prevKey,
                nextKey = nextKey
            )
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}