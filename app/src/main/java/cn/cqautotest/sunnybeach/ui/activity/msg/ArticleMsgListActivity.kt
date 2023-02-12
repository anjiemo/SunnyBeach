package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.ArticleMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.ArticleMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 文章消息列表界面
 */
class ArticleMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding<ArticleMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mArticleMsgAdapter = ArticleMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mArticleMsgAdapter

    override fun getLayoutId(): Int = R.layout.article_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getArticleMsgList().collectLatest { mArticleMsgAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mArticleMsgAdapter.snapshotList[position]?.let {
                it.hasRead = "1"
                mArticleMsgAdapter.notifyItemChanged(position)
                val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${it.articleId}"
                mMsgViewModel.readArticleMsg(it.id).observe(this) {}
                BrowserActivity.start(this, url)
            }
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }
}