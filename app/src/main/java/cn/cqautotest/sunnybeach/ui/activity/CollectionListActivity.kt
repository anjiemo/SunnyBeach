package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.databinding.CollectionListActivityBinding
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.Bookmark
import cn.cqautotest.sunnybeach.ui.adapter.CollectionListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.viewmodel.CollectionViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹列表页
 */
class CollectionListActivity : PagingActivity<Bookmark.Content, CollectionListAdapter.CollectionListViewHolder>(), StatusAction {

    private val mBinding by viewBinding<CollectionListActivityBinding>()
    private val mCollectionViewModel by viewModels<CollectionViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mCollectionListAdapter = CollectionListAdapter(mAdapterDelegate)

    override fun getLayoutId(): Int = R.layout.collection_list_activity

    override suspend fun loadListData() {
        mCollectionViewModel.loadCollectionList().collectLatest {
            mCollectionListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到收藏夹详情界面
            val item = mCollectionListAdapter.snapshotList[position] ?: return@setOnItemClickListener
            CollectionDetailListActivity.start(this, item)
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlCollectionListHint

    override fun getRecyclerView(): RecyclerView = mBinding.rvCollectionList

    override fun getPagingAdapter() = mCollectionListAdapter

    override fun getRefreshLayout(): RefreshLayout = mBinding.refreshLayout
}