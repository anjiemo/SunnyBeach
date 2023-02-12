package cn.cqautotest.sunnybeach.app

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.ui.delegate.PagingUiDelegate
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 使用 Paging3 分页的 Activity 基类
 */
abstract class PagingActivity : AppActivity(), StatusAction {

    private val refreshLayout by lazy { requireNotNull(findViewById<SmartRefreshLayout>(R.id.paging_refresh_layout)) { checkLayoutIdTips("paging_refresh_layout") } }
    private val recyclerView by lazy { requireNotNull(findViewById<RecyclerView>(R.id.paging_recycler_view)) { checkLayoutIdTips("paging_recycler_view") } }
    private val pagingDataAdapter by lazy { getPagingAdapter() }
    private val mPagingUiDelegate by lazy { PagingUiDelegate(lifecycle, this, refreshLayout, recyclerView, pagingDataAdapter) }

    private fun checkLayoutIdTips(idName: String) = "are you ok? Do you have a control with id $idName in your xml layout? "

    override fun getStatusLayout(): StatusLayout? = findViewById<StatusLayout>(R.id.paging_status_layout)

    abstract fun getPagingAdapter(): PagingDataAdapter<*, *>

    @CallSuper
    override fun initView() {
        mPagingUiDelegate.initView()
    }

    override fun initData() {
        lifecycleScope.launch { repeatOnLifecycle(Lifecycle.State.RESUMED) { loadListData() } }
    }

    abstract suspend fun loadListData()

    @CallSuper
    override fun initEvent() {
        mPagingUiDelegate.initEvent()
    }
}
