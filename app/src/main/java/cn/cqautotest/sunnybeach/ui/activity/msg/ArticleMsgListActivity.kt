package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ArticleMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.ArticleMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 文章消息列表界面
 */
class ArticleMsgListActivity : AppActivity(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<ArticleMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mArticleMsgAdapter = ArticleMsgAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mArticleMsgAdapter) { mBinding.refreshLayout.finishRefresh() }

    override fun getLayoutId(): Int = R.layout.article_msg_list_activity

    override fun initView() {
        mBinding.rvArticleMsgList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mArticleMsgAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            mMsgViewModel.getArticleMsgList().collectLatest {
                mArticleMsgAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        getTitleBar()?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mArticleMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mArticleMsgAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            val item = mArticleMsgAdapter.snapshotList[position] ?: return@setOnItemClickListener
            val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${item.articleId}"
            mMsgViewModel.readArticleMsg(item.id).observe(this) {}
            BrowserActivity.start(this, url)
        }
    }

    override fun onBack2Top() {
        mBinding.rvArticleMsgList.scrollToPosition(0)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlArticleMsgHint

    override fun onDestroy() {
        super.onDestroy()
        mArticleMsgAdapter.removeLoadStateListener(loadStateListener)
    }
}