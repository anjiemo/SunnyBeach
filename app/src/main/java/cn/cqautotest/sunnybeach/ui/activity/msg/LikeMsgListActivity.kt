package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.LikeMsgListActivityBinding
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.LikeMsgAdapter
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
 * desc   : 点赞消息列表界面
 */
class LikeMsgListActivity : AppActivity(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<LikeMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mLikeMsgAdapter = LikeMsgAdapter(AdapterDelegate())
    private val loadStateListener = loadStateListener(mLikeMsgAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.like_msg_list_activity

    override fun initView() {
        mBinding.rvLikeMsgList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mLikeMsgAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenCreated {
            mMsgViewModel.getLikeMsgList().collectLatest {
                mLikeMsgAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        titleBar?.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mLikeMsgAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mLikeMsgAdapter.addLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.rvLikeMsgList.scrollToPosition(0)
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlLikeMsgHint

    override fun onDestroy() {
        super.onDestroy()
        mLikeMsgAdapter.removeLoadStateListener(loadStateListener)
    }
}