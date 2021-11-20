package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserFollowListFragmentBinding
import cn.cqautotest.sunnybeach.other.FollowState
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.UserFollowListAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户关注的用户列表 Fragment
 */
class UserFollowListFragment : AppFragment<AppActivity>(), StatusAction {

    private val mBinding by viewBinding<UserFollowListFragmentBinding>()
    private val mUserFollowListAdapter = UserFollowListAdapter(AdapterDelegate())
    private val mUserViewModel by viewModels<UserViewModel>()
    private val loadStateListener = { cls: CombinedLoadStates ->
        when (cls.refresh) {
            is LoadState.NotLoading -> {
                mBinding.refreshLayout.finishRefresh()
                if (mUserFollowListAdapter.isEmpty()) {
                    showEmpty()
                } else {
                    showComplete()
                }
            }
            is LoadState.Loading -> showLoading()
            is LoadState.Error -> showError { mUserFollowListAdapter.refresh() }
        }
    }

    override fun getLayoutId(): Int = R.layout.user_follow_list_fragment

    override fun initView() {
        mBinding.rvFollowList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mUserFollowListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val userId = arguments?.getString(IntentKey.ID, "") ?: ""
        val followStateJson = arguments?.getString(IntentKey.OTHER)
        val followState = fromJson<FollowState>(followStateJson) ?: FollowState.FOLLOW
        loadUserFollowList(userId, followState)
    }

    private fun loadUserFollowList(userId: String, followState: FollowState) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mUserViewModel.getUserFollowList(userId, followState).collectLatest {
                mUserFollowListAdapter.submitData(it)
            }
        }
    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener {
            mUserFollowListAdapter.refresh()
        }
        // 需要在 View 销毁的时候移除 listener
        mUserFollowListAdapter.addLoadStateListener(loadStateListener)
        mUserFollowListAdapter.setOnItemClickListener { item, _ ->
            // 跳转到用户详情界面
            ViewUserActivity.start(requireContext(), item.userId)
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlFollowHint

    override fun onDestroyView() {
        super.onDestroyView()
        mUserFollowListAdapter.removeLoadStateListener(loadStateListener)
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String, followState: FollowState): UserFollowListFragment {
            val fragment = UserFollowListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
                putString(IntentKey.OTHER, followState.toJson())
            }
            fragment.arguments = args
            return fragment
        }
    }
}