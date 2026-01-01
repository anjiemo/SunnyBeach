package cn.cqautotest.sunnybeach.ui.fragment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingTitleBarFragment
import cn.cqautotest.sunnybeach.databinding.QaListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.model.RefreshStatus
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.QaListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.QaViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.LinearSpaceDecoration
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 问答列表 Fragment
 */
class QaListFragment : PagingTitleBarFragment<AppActivity>(), OnBack2TopListener {

    private val mBinding by viewBinding(QaListFragmentBinding::bind)
    private val mQaViewModel by activityViewModels<QaViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mQaListAdapter = QaListAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mQaListAdapter

    override fun getLayoutId(): Int = R.layout.qa_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(LinearSpaceDecoration(mainSpace = 4.dp, crossSpace = 0))
    }

    override suspend fun loadListData() {
        mQaViewModel.qaListFlow.collectLatest {
            onBack2Top()
            mQaListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        mBinding.titleBar.setDoubleClickListener { onBack2Top() }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到问答详情界面
            mQaListAdapter.snapshotList.getOrNull(position)?.let {
                val url = "$SUNNY_BEACH_QA_URL_PRE${it.id}"
                BrowserActivity.start(requireContext(), url)
            }
        }
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun isStatusBarEnabled() = true

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }

    companion object {

        @JvmStatic
        fun newInstance(): QaListFragment {
            val fragment = QaListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}