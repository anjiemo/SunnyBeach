package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.AtMeMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.AtMeMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : @我 消息列表界面
 */
class AtMeMsgListActivity : AppActivity(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<AtMeMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mAtMeMsgAdapter = AtMeMsgAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mAtMeMsgAdapter) { mBinding.refreshLayout.finishRefresh() }

    override fun getLayoutId(): Int = R.layout.at_me_msg_list_activity

    override fun initView() {
        mBinding.rvAtMeMsgList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAtMeMsgAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            mMsgViewModel.getAtMeMsgList().collectLatest {
                mAtMeMsgAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        getTitleBar()?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mAtMeMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mAtMeMsgAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mAtMeMsgAdapter.snapshotList[position]?.let {
                mMsgViewModel.readAtMeMsg(it.id).observe(this) {}
                when (it.type) {
                    "moment" -> FishPondDetailActivity.start(this, it.exId)
                    "wenda" -> {
                        val url = "$SUNNY_BEACH_QA_URL_PRE${it.exId}"
                        BrowserActivity.start(this, url)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onBack2Top() {
        mBinding.rvAtMeMsgList.scrollToPosition(0)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlAtMeMsgHint

    override fun onDestroy() {
        super.onDestroy()
        mAtMeMsgAdapter.removeLoadStateListener(loadStateListener)
    }
}