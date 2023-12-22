package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.SystemMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.addAfterNextUpdateUIDefaultItemAnimator
import cn.cqautotest.sunnybeach.ktx.clearItemAnimator
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.SystemMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 系统消息列表界面
 */
class SystemMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding<SystemMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mSystemMsgAdapter = SystemMsgAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mSystemMsgAdapter) {
        mBinding.refreshLayout.finishRefresh()
        mBinding.rvSystemMsgList.addAfterNextUpdateUIDefaultItemAnimator()
    }

    override fun getPagingAdapter() = mSystemMsgAdapter

    override fun getLayoutId(): Int = R.layout.system_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        mBinding.rvSystemMsgList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mSystemMsgAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
            clearItemAnimator()
        }
    }

    override suspend fun loadListData() {
        mMsgViewModel.getSystemMsgList().collectLatest { mSystemMsgAdapter.submitData(it) }
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
            mSystemMsgAdapter.snapshotList.getOrNull(position)?.let { msg ->
                val url = when (msg.exType) {
                    // 文章
                    "article" -> "$SUNNY_BEACH_ARTICLE_URL_PRE${msg.exId}"
                    // 登录奖励
                    "sobTrade" -> null
                    // 问答评论
                    "wendaComment" -> "$SUNNY_BEACH_QA_URL_PRE${msg.exId}"
                    else -> null
                }
                url?.let { BrowserActivity.start(this, it) }
            }
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
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