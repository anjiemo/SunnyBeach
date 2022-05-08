package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserFishListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.UserQaListAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_QA_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.QaViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户回答列表 Fragment
 */
class UserQaListFragment : AppFragment<AppActivity>(), StatusAction {

    private val mBinding by viewBinding<UserFishListFragmentBinding>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mUserQaListAdapter = UserQaListAdapter(mAdapterDelegate)
    private val mQaViewModel by activityViewModels<QaViewModel>()
    private val loadStateListener = loadStateListener(mUserQaListAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.user_fish_list_fragment

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mUserQaListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val userId = arguments?.getString(IntentKey.ID, "") ?: ""
        loadUserQaList(userId)
    }

    private fun loadUserQaList(userId: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mQaViewModel.loadUserQaList(userId).collectLatest {
                mUserQaListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mUserQaListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mUserQaListAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到问答详情界面
            val item = mUserQaListAdapter.snapshot()[position] ?: return@setOnItemClickListener
            val url = "$SUNNY_BEACH_QA_URL_PRE${item.wendaComment.wendaId}"
            BrowserActivity.start(requireContext(), url)
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun onDestroyView() {
        super.onDestroyView()
        mUserQaListAdapter.removeLoadStateListener(loadStateListener)
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String): UserQaListFragment {
            val fragment = UserQaListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}