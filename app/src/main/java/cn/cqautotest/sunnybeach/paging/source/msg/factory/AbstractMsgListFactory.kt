package cn.cqautotest.sunnybeach.paging.source.msg.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.model.IApiResponse
import cn.cqautotest.sunnybeach.paging.source.msg.impl.IMsgContent
import cn.cqautotest.sunnybeach.paging.source.msg.impl.IMsgPageData
import cn.cqautotest.sunnybeach.paging.source.msg.impl.MsgPagingSourceImpl
import com.huawei.hms.scankit.p.T
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 抽象列表消息 PagingSource 工厂
 */
abstract class AbstractMsgListFactory {

    abstract suspend fun createMsgListByType(page: Int): IApiResponse<IMsgPageData>

    context (ViewModel)
    fun <T : IMsgContent> get(): Flow<PagingData<T>> {
        return Pager(config = PagingConfig(DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                MsgPagingSourceImpl<T>(this)
            }).flow.cachedIn(viewModelScope)
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 30
    }
}
