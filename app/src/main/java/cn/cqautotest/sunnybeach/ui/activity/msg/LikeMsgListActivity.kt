package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.LikeMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.LikeMsgAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 点赞消息列表界面
 */
class LikeMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding<LikeMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mLikeMsgAdapter = LikeMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mLikeMsgAdapter

    override fun getLayoutId(): Int = R.layout.like_msg_list_activity

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getLikeMsgList().collectLatest {
            mLikeMsgAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener {
            onBack2Top()
        }
        mAdapterDelegate.setOnItemClickListener { _, _ ->

        }
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }
}