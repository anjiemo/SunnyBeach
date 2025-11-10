package cn.cqautotest.sunnybeach.ui.activity.msg

import android.view.View
import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.QaMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.msg.QaMsg
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.QaMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.SimpleLinearSpaceItemDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : 问答消息列表界面
 */
class QaMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding(QaMsgListActivityBinding::bind)
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mQaMsgAdapter = QaMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mQaMsgAdapter

    override fun getLayoutId(): Int = R.layout.qa_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getQaMsgList().collectLatest { mQaMsgAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { view, position ->
            mQaMsgAdapter.snapshotList.getOrNull(position)?.let {
                handleItemClick(view, it, position)
            }
        }
    }

    fun handleItemClick(view: View, content: QaMsg.Content, position: Int) {
        when (view.id) {
            R.id.iv_avatar, R.id.ll_top_container, R.id.tv_desc -> {
                ViewUserActivity.start(this, content.uid)
            }

            else -> {
                content.hasRead = "1"
                mQaMsgAdapter.notifyItemChanged(position)
                val url = "$SUNNY_BEACH_QA_URL_PRE${content.wendaId}"
                BrowserActivity.start(this, url)
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