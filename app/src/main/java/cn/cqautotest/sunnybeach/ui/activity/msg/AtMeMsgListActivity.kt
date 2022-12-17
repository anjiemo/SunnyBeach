package cn.cqautotest.sunnybeach.ui.activity.msg

import androidx.activity.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.AtMeMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.AtMeMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : @我 消息列表界面
 */
class AtMeMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding<AtMeMsgListActivityBinding>()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mAtMeMsgAdapter = AtMeMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mAtMeMsgAdapter

    override fun getLayoutId(): Int = R.layout.at_me_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getAtMeMsgList().collectLatest { mAtMeMsgAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mAtMeMsgAdapter.snapshotList[position]?.let {
                it.hasRead = "1"
                mAtMeMsgAdapter.notifyItemChanged(position)
                mMsgViewModel.readAtMeMsg(it.id).observe(this) {}
                when (it.type) {
                    "article" -> {
                        val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${it.exId}"
                        BrowserActivity.start(this, url)
                    }
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

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }
}