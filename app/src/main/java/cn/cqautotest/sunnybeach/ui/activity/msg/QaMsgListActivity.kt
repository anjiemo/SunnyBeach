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
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.QaMsgAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.loadStateListener
import cn.cqautotest.sunnybeach.util.setDoubleClickListener
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
    private val mQaMsgAdapter = QaMsgAdapter(AdapterDelegate())
    private val loadStateListener = loadStateListener(mQaMsgAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

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
        titleBar?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mQaMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mQaMsgAdapter.addLoadStateListener(loadStateListener)
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