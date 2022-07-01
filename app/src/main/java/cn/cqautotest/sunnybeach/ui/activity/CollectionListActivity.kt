package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.CollectionListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.adapter.CollectionListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.CollectionViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹列表页
 */
class CollectionListActivity : PagingActivity() {

    private val mBinding by viewBinding<CollectionListActivityBinding>()
    private val mCollectionViewModel by viewModels<CollectionViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mCollectionListAdapter = CollectionListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mCollectionListAdapter

    override fun getLayoutId(): Int = R.layout.collection_list_activity

    override suspend fun loadListData() = mCollectionViewModel.loadCollectionList().collectLatest { getPagingAdapter().submitData(it) }

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
    }

    override fun initEvent() {
        super.initEvent()
        // 跳转到收藏夹详情界面
        mAdapterDelegate.setOnItemClickListener { _, position ->
            getPagingAdapter().snapshotList[position]?.let { CollectionDetailListActivity.start(this, it) }
        }
    }
}
