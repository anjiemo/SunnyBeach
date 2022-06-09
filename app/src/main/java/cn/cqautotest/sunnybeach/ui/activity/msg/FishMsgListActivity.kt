package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.FishMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.MomentMsgAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 摸鱼消息列表界面
 */
class FishMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding<FishMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mMomentMsgAdapter = MomentMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mMomentMsgAdapter

    override fun getLayoutId(): Int = R.layout.fish_msg_list_activity

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getMomentMsgList().collectLatest {
            mMomentMsgAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mMomentMsgAdapter.snapshotList[position]?.let {
                mMsgViewModel.readMomentMsg(it.id).observe(this) {}
                FishPondDetailActivity.start(this, it.momentId)
            }
        }
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }
}