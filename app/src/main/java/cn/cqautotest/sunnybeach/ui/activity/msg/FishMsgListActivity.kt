package cn.cqautotest.sunnybeach.ui.activity.msg

import android.view.View
import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.FishMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.msg.MomentMsg
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.MomentMsgAdapter
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.LinearSpaceDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 摸鱼消息列表界面
 */
class FishMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding(FishMsgListActivityBinding::bind)
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mMomentMsgAdapter = MomentMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mMomentMsgAdapter

    override fun getLayoutId(): Int = R.layout.fish_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(LinearSpaceDecoration(mainSpace = 1.dp, crossSpace = 0))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getMomentMsgList().collectLatest { mMomentMsgAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { view, position ->
            mMomentMsgAdapter.snapshotList.getOrNull(position)?.let {
                handleItemClick(view, it, position)
            }
        }
    }

    fun handleItemClick(view: View, content: MomentMsg.Content, position: Int) {
        when (view.id) {
            R.id.iv_avatar, R.id.ll_top_container, R.id.tv_desc -> {
                ViewUserActivity.start(this, content.uid)
            }

            else -> {
                content.hasRead = "1"
                mMomentMsgAdapter.notifyItemChanged(position)
                mMsgViewModel.readMomentMsg(content.id).observe(this) {}
                FishPondDetailActivity.start(this, content.momentId)
            }
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }
}