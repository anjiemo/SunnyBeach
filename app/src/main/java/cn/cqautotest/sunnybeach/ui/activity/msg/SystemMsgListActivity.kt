package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SystemMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.SystemMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 系统消息列表界面
 */
class SystemMsgListActivity : AppActivity(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<SystemMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mSystemMsgAdapter = SystemMsgAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mSystemMsgAdapter) { mBinding.refreshLayout.finishRefresh() }

    override fun getLayoutId(): Int = R.layout.system_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        mBinding.rvSystemMsgList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mSystemMsgAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            mMsgViewModel.getSystemMsgList().collectLatest {
                mSystemMsgAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        getTitleBar()?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mSystemMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mSystemMsgAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mSystemMsgAdapter.snapshotList[position]?.let {
                val url = when (it.exType) {
                    // 文章
                    "article" -> "$SUNNY_BEACH_ARTICLE_URL_PRE${it.exId}"
                    // 登录奖励
                    "sobTrade" -> null
                    // 问答评论
                    "wendaComment" -> "$SUNNY_BEACH_QA_URL_PRE${it.exId}"
                    else -> null
                }
                url?.let { BrowserActivity.start(this, it) }
            }
        }
    }

    override fun onBack2Top() {
        mBinding.rvSystemMsgList.scrollToPosition(0)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlSystemMsgHint

    override fun onDestroy() {
        super.onDestroy()
        mSystemMsgAdapter.removeLoadStateListener(loadStateListener)
    }
}