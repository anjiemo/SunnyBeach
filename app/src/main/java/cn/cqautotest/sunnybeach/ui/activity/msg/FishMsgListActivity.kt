package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.MomentMsgAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 摸鱼消息列表界面
 */
class FishMsgListActivity : AppActivity(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<FishMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mMomentMsgAdapter = MomentMsgAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mMomentMsgAdapter) { mBinding.refreshLayout.finishRefresh() }

    override fun getLayoutId(): Int = R.layout.fish_msg_list_activity

    override fun initView() {
        mBinding.rvFishMsgList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mMomentMsgAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            mMsgViewModel.getMomentMsgList().collectLatest {
                mMomentMsgAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        getTitleBar()?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mMomentMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mMomentMsgAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            val item = mMomentMsgAdapter.snapshotList[position] ?: return@setOnItemClickListener
            mMsgViewModel.readMomentMsg(item.id).observe(this) {}
            FishPondDetailActivity.start(this, item.momentId)
        }
    }

    override fun onBack2Top() {
        mBinding.rvFishMsgList.scrollToPosition(0)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishMsgHint

    override fun onDestroy() {
        super.onDestroy()
        mMomentMsgAdapter.removeLoadStateListener(loadStateListener)
    }
}