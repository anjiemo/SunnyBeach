package cn.cqautotest.sunnybeach.paging.source.msg.impl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.cqautotest.sunnybeach.db.dao.UserBlockDao
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.paging.source.msg.factory.AbstractMsgListFactory
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicBoolean

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/27
 * desc   : 列表消息 PagingSource 实现类
 */
class MsgPagingSourceImpl<T : IMsgContent>(
    private val msgListFactory: AbstractMsgListFactory,
    private val msgType: MsgType,
    private val userId: String,
    private val userBlockDao: UserBlockDao
) :
    PagingSource<Int, T>() {

    private val isLoadBlockList = AtomicBoolean()
    private val blockedUserSet = CopyOnWriteArraySet<String>()

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> page is $page")
            val response = try {
                msgListFactory.createMsgListByType(page)
            } catch (e: ClassCastException) {
                throw ClassCastException("Are you ok? This class can't cast to IApiResponse<IMsgPageData>.")
            }
            val responseData = response.getData()
            val prevKey = if (responseData.isFirst()) null else page - 1
            val nextKey = if (responseData.isLast()) null else page + 1
            if (response.isSuccess()) LoadResult.Page(
                data = getFilteredMsgContentList(responseData.getMsgContentList()) as List<T>,
                prevKey = prevKey,
                nextKey = nextKey
            )
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    private suspend fun getFilteredMsgContentList(msgContentList: List<IMsgContent>): List<IMsgContent> {
        // 先确保黑名单已加载（仅首次执行）
        loadBlockedUsersIfNeeded()

        // 根据消息类型决定是否过滤
        return when (msgType) {
            MsgType.ARTICLE, MsgType.FISH, MsgType.QA, MsgType.LIKE, MsgType.AT ->
                msgContentList.filterNot { it.isFromBlockedUser() }

            MsgType.SYSTEM -> msgContentList
        }
    }

    /**
     * 仅在未加载过黑名单且黑名单为空时，加载并缓存黑名单
     * 利用AtomicBoolean确保并发场景下仅加载一次
     */
    private suspend fun loadBlockedUsersIfNeeded() {
        // 双重校验：先判断状态，避免不必要的原子操作
        if (!isLoadBlockList.get() && blockedUserSet.isEmpty()) {
            // 原子操作：尝试将状态改为true，只有首次调用会返回false
            val shouldLoad = !isLoadBlockList.getAndSet(true)
            if (shouldLoad) {
                val blockedUsers = userBlockDao.getBlockedUsers(userId)
                blockedUserSet.addAll(blockedUsers)
            }
        }
    }

    /**
     * 扩展函数：判断消息是否来自黑名单用户
     * 封装判断逻辑，提升可读性
     */
    private fun IMsgContent.isFromBlockedUser(): Boolean {
        return this is IUserMsgContent && this.getUId() in blockedUserSet
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}
