package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.CollectionDetailListActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.Bookmark
import cn.cqautotest.sunnybeach.ui.adapter.CollectionDetailListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.CollectionViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import com.dylanc.longan.intentExtras
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹详情列表页
 */
class CollectionDetailListActivity : AppActivity(), StatusAction {

    private val mBinding by viewBinding<CollectionDetailListActivityBinding>()
    private val mCollectionViewModel by viewModels<CollectionViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mCollectionDetailListAdapter = CollectionDetailListAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mCollectionDetailListAdapter) { mBinding.refreshLayout.finishRefresh() }
    private val collectionItemJson by intentExtras<String>(COLLECTION_ITEM)
    private val collectionItem by lazy { fromJson<Bookmark.Content>(collectionItemJson) }

    override fun getLayoutId(): Int = R.layout.collection_detail_list_activity

    override fun initView() {
        with(mBinding) {
            titleBar.title = collectionItem.name
            rvCollectionDetailList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mCollectionDetailListAdapter
                addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
            }
        }
    }

    override fun initData() {
        loadCollectionList()
    }

    private fun loadCollectionList() {
        lifecycleScope.launchWhenCreated {
            mCollectionViewModel.loadCollectionDetailListById(collectionItem.id).collectLatest {
                mCollectionDetailListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener { mCollectionDetailListAdapter.refresh() }
        // 需要在 View 销毁的时候移除 listener
        mCollectionDetailListAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到收藏内容详情界面
            val item = mCollectionDetailListAdapter.snapshotList[position] ?: return@setOnItemClickListener
            BrowserActivity.start(this, item.url)
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlCollectionDetailListHint

    override fun onDestroy() {
        super.onDestroy()
        mCollectionDetailListAdapter.removeLoadStateListener(loadStateListener)
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