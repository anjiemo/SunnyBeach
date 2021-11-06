package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ArticleMsgListActivityBinding
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.ArticleMsgAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.isEmpty
import cn.cqautotest.sunnybeach.util.setDoubleClickListener
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/20
 * desc   : 文章消息列表界面
 */
class ArticleMsgListActivity : AppActivity(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<ArticleMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mArticleMsgAdapter = ArticleMsgAdapter(AdapterDelegate())
    private val loadStateListener = { cls: CombinedLoadStates ->
        when (cls.refresh) {
            is LoadState.NotLoading -> {
                mBinding.refreshLayout.finishRefresh()
                if (mArticleMsgAdapter.isEmpty()) {
                    showEmpty()
                } else {
                    showComplete()
                }
            }
            is LoadState.Loading -> showLoading()
            is LoadState.Error -> showError { mArticleMsgAdapter.refresh() }
        }
    }

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
        titleBar?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mArticleMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mArticleMsgAdapter.addLoadStateListener(loadStateListener)
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