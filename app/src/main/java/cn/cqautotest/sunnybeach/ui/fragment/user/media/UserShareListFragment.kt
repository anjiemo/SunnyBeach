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
import cn.cqautotest.sunnybeach.ui.adapter.ShareListAdapter
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_SHARE_URL_PRE
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.ShareViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 用户分享列表 Fragment
 */
class UserShareListFragment : AppFragment<AppActivity>(), StatusAction {

    private val mBinding by viewBinding<UserFishListFragmentBinding>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mShareListAdapter = ShareListAdapter(mAdapterDelegate)
    private val mShareViewModel by activityViewModels<ShareViewModel>()
    private val loadStateListener = loadStateListener(mShareListAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.user_fish_list_fragment

    override fun initView() {
        mBinding.rvFishPondList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mShareListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val userId = arguments?.getString(IntentKey.ID, "") ?: ""
        loadUserQaList(userId)
    }

    private fun loadUserQaList(userId: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mShareViewModel.loadUserShareList(userId).collectLatest {
                mShareListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mShareListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mShareListAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到分享详情界面
            val item = mShareListAdapter.snapshot()[position] ?: return@setOnItemClickListener
            val url = "$SUNNY_BEACH_SHARE_URL_PRE${item.id}"
            BrowserActivity.start(requireContext(), url)
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFishPondHint

    override fun onDestroyView() {
        super.onDestroyView()
        mShareListAdapter.removeLoadStateListener(loadStateListener)
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String): UserShareListFragment {
            val fragment = UserShareListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}