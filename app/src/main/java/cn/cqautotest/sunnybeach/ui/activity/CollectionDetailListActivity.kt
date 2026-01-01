package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.CollectionDetailListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.Bookmark
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.CollectionDetailListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.viewmodel.CollectionViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.LinearSpaceDecoration
import com.dylanc.longan.intentExtras
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹详情列表页
 */
class CollectionDetailListActivity : PagingActivity() {

    private val mBinding by viewBinding(CollectionDetailListActivityBinding::bind)
    private val mCollectionViewModel by viewModels<CollectionViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mCollectionDetailListAdapter = CollectionDetailListAdapter(mAdapterDelegate)
    private val collectionItemJson by intentExtras<String>(COLLECTION_ITEM)
    private val collectionItem by lazy { fromJson<Bookmark.Content>(collectionItemJson) }

    override fun getPagingAdapter() = mCollectionDetailListAdapter

    override fun getLayoutId(): Int = R.layout.collection_detail_list_activity

    override fun initView() {
        super.initView()
        with(mBinding) {
            titleBar.title = collectionItem.name
            pagingRecyclerView.addItemDecoration(LinearSpaceDecoration(mainSpace = 4.dp, crossSpace = 0))
        }
    }

    override suspend fun loadListData() {
        mCollectionViewModel.collectionDetailListFlow.collectLatest { mCollectionDetailListAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        // 跳转到收藏内容详情界面
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mCollectionDetailListAdapter.snapshotList.getOrNull(position)?.let { BrowserActivity.start(this, it.url) }
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    companion object {

        private const val COLLECTION_ITEM = "collectionItem"

        fun start(context: Context, item: Bookmark.Content) {
            context.startActivity<CollectionDetailListActivity> {
                putExtra(IntentKey.ID, item.id)
                putExtra(COLLECTION_ITEM, item.toJson())
            }
        }
    }
}