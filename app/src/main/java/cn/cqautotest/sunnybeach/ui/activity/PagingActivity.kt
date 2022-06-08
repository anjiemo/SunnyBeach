package cn.cqautotest.sunnybeach.ui.activity

import androidx.annotation.CallSuper
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 使用 Paging3 分页的 Activity 基类
 */
abstract class PagingActivity<T : Any, VH : RecyclerView.ViewHolder> : AppActivity(), StatusAction {

    private val loadStateListener by lazy { loadStateListener(getPagingAdapter()) { getRefreshLayout().finishRefresh() } }

    abstract fun getRecyclerView(): RecyclerView

    abstract fun getPagingAdapter(): PagingDataAdapter<T, VH>

    abstract fun getRefreshLayout(): RefreshLayout

    @CallSuper
    override fun initView() {
        getRecyclerView().apply {
            layoutManager = LinearLayoutManager(context)
            adapter = getPagingAdapter()
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    @CallSuper
    override fun initData() {
        lifecycleScope.launchWhenCreated { loadListData() }
    }

    abstract suspend fun loadListData()

    @CallSuper
    override fun initEvent() {
        getRefreshLayout().setOnRefreshListener { getPagingAdapter().refresh() }
        // 需要在 View 销毁的时候移除 listener
        getPagingAdapter().addLoadStateListener(loadStateListener)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        getPagingAdapter().removeLoadStateListener(loadStateListener)
    }
}