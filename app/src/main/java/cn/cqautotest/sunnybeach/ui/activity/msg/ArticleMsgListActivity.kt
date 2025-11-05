package cn.cqautotest.sunnybeach.ui.activity.msg

import android.view.View
import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.ArticleMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.msg.ArticleMsg
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.ArticleMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 文章消息列表界面
 */
class ArticleMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding(ArticleMsgListActivityBinding::bind)
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
        mAdapterDelegate.setOnItemClickListener { view, position ->
            mArticleMsgAdapter.snapshotList.getOrNull(position)?.let {
                handleItemClick(view, it, position)
            }
        }
    }

    private fun handleItemClick(view: View, content: ArticleMsg.Content, position: Int) {
        when (view.id) {
            R.id.iv_avatar, R.id.ll_top_container, R.id.tv_desc -> {
                ViewUserActivity.start(this, content.uid)
            }

            else -> {
                content.hasRead = "1"
                mArticleMsgAdapter.notifyItemChanged(position)
                val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${content.articleId}"
                mMsgViewModel.readArticleMsg(content.id).observe(this) {}
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