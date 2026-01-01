package cn.cqautotest.sunnybeach.ui.activity.msg

import android.view.View
import androidx.activity.viewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.PagingActivity
import cn.cqautotest.sunnybeach.databinding.AtMeMsgListActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.model.msg.AtMeMsg
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.msg.AtMeMsgAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_ARTICLE_URL_PRE
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.LinearSpaceDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/24
 * desc   : @我 消息列表界面
 */
class AtMeMsgListActivity : PagingActivity(), OnBack2TopListener {

    private val mBinding by viewBinding(AtMeMsgListActivityBinding::bind)
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mAtMeMsgAdapter = AtMeMsgAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mAtMeMsgAdapter

    override fun getLayoutId(): Int = R.layout.at_me_msg_list_activity

    override fun initView() {
        hideSupportActionBar()
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(LinearSpaceDecoration(mainSpace = 1.dp, crossSpace = 0))
    }

    override suspend fun loadListData() {
        mMsgViewModel.getAtMeMsgList().collectLatest { mAtMeMsgAdapter.submitData(it) }
    }

    override fun initEvent() {
        super.initEvent()
        getTitleBar()?.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { view, position ->
            mAtMeMsgAdapter.snapshotList.getOrNull(position)?.let {
                handleItemClick(view, it, position)
            }
        }
    }

    private fun handleItemClick(view: View, content: AtMeMsg.Content, position: Int) {
        when (view.id) {
            R.id.iv_avatar, R.id.ll_top_container, R.id.tv_desc -> {
                ViewUserActivity.start(this, content.uid)
            }

            else -> {
                content.hasRead = "1"
                mAtMeMsgAdapter.notifyItemChanged(position)
                mMsgViewModel.readAtMeMsg(content.id).observe(this) {}
                when (content.type) {
                    "article" -> {
                        val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${content.exId}"
                        BrowserActivity.start(this, url)
                    }

                    "moment" -> FishPondDetailActivity.start(this, content.exId)
                    "wenda" -> {
                        val url = "$SUNNY_BEACH_QA_URL_PRE${content.exId}"
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