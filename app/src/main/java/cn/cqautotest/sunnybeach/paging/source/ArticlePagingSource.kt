package cn.cqautotest.sunnybeach.paging.source

import androidx.core.text.isDigitsOnly
import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.http.api.sob.HomeApi
import cn.cqautotest.sunnybeach.model.ArticleInfo
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 文章 PagingSource
 */
class ArticlePagingSource(private val categoryId: String) :
    PagingSource<Int, ArticleInfo.ArticleItem>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleInfo.ArticleItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleInfo.ArticleItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> categoryId is $categoryId page is $page")
            val response = if (categoryId.isEmpty() || categoryId.isDigitsOnly().not()) {
                HomeApi.getRecommendContent(page = page)
            } else {
                HomeApi.getArticleListByCategoryId(categoryId = categoryId, page = page)
            }
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