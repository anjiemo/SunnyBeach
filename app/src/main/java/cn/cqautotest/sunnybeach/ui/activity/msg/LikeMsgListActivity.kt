package cn.cqautotest.sunnybeach.ui.activity.msg

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.LikeMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.msg.LikeMsg
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.LikeMsgAdapter
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.UniversalSpaceDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 点赞消息列表界面
 */
class LikeMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding(LikeMsgListActivityBinding::bind)
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mLikeMsgAdapter = LikeMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mLikeMsgAdapter

    override fun getLayoutId(): Int = R.layout.like_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(UniversalSpaceDecoration(mainSpace = 1.dp, crossSpace = 0))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getLikeMsgList()
            .flowWithLifecycle(lifecycle)
            .collectLatest { mLikeMsgAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { view, position ->
            mLikeMsgAdapter.snapshotList.getOrNull(position)?.let {
                handleItemClick(view, it, position)
            }
        }
    }

    fun handleItemClick(view: View, content: LikeMsg.Content, position: Int) {
        when (view.id) {
            R.id.iv_avatar, R.id.ll_top_container, R.id.tv_desc -> {
                ViewUserActivity.start(this, content.uid)
            }

            else -> {
                content.hasRead = "1"
                mLikeMsgAdapter.notifyItemChanged(position)
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