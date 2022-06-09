package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.QaMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.QaMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 问答消息列表界面
 */
class QaMsgListActivity : AppActivity(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<QaMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mQaMsgAdapter = QaMsgAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mQaMsgAdapter) { mBinding.refreshLayout.finishRefresh() }

    override fun getLayoutId(): Int = R.layout.qa_msg_list_activity

    override fun initView() {
        mBinding.rvQaMsgList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mQaMsgAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            mMsgViewModel.getQaMsgList().collectLatest {
                mQaMsgAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        getTitleBar()?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mQaMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mQaMsgAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到问答详情界面
            mQaMsgAdapter.snapshotList[position]?.let {
                val url = "$SUNNY_BEACH_QA_URL_PRE${it.wendaId}"
                BrowserActivity.start(this, url)
            }
        }
    }

    override fun onBack2Top() {
        mBinding.rvQaMsgList.scrollToPosition(0)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlQaMsgHint

    override fun onDestroy() {
        super.onDestroy()
        mQaMsgAdapter.removeLoadStateListener(loadStateListener)
    }
}