package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.CollectionDetailListActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.Bookmark
import cn.cqautotest.sunnybeach.ui.adapter.CollectionDetailListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.CollectionViewModel
import com.dylanc.longan.intentExtras
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹详情列表页
 */
class CollectionDetailListActivity : PagingActivity() {

    private val mBinding by viewBinding<CollectionDetailListActivityBinding>()
    private val mCollectionViewModel by viewModels<CollectionViewModel>()
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
            pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override suspend fun loadListData() {
        mCollectionViewModel.loadCollectionDetailListById(collectionItem.id).collectLatest { mCollectionDetailListAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        // 跳转到收藏内容详情界面
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mCollectionDetailListAdapter.snapshotList[position]?.let { BrowserActivity.start(this, it.url) }
        }
    }

    companion object {

        private const val COLLECTION_ITEM = "collectionItem"

        fun start(context: Context, item: Bookmark.Content) {
            context.startActivity<CollectionDetailListActivity> {
                putExtra(COLLECTION_ITEM, item.toJson())
            }
        }
    }
}