package cn.cqautotest.sunnybeach.ui.fragment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.QaListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.setDoubleClickListener
import cn.cqautotest.sunnybeach.other.QaType
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.QaListAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.QaViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/18
 * desc   : 问答列表 Fragment
 */
class QaListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<QaListFragmentBinding>()
    private val mQaViewModel by activityViewModels<QaViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mQaListAdapter = QaListAdapter(mAdapterDelegate)
    private val loadStateListener = loadStateListener(mQaListAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.qa_list_fragment

    override fun initView() {
        mBinding.rvQaList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mQaListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        loadQaList()
    }

    private fun loadQaList() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mQaViewModel.loadQaList(QaType.LATEST).collectLatest {
                onBack2Top()
                mQaListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.titleBar.setDoubleClickListener {
            onBack2Top()
        }
        mBinding.refreshLayout.setOnRefreshListener {
            mQaListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mQaListAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到问答详情界面
            val item = mQaListAdapter.snapshot()[position] ?: return@setOnItemClickListener
            val url = "$SUNNY_BEACH_QA_URL_PRE${item.id}"
            BrowserActivity.start(requireContext(), url)
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlQaHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mQaListAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.rvQaList.scrollToPosition(0)
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