package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.StatusAction
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserFollowOrFansListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.loadStateListener
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.other.FollowType
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.UserFollowListAdapter
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.FollowOrFansViewModel
import cn.cqautotest.sunnybeach.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户关注/粉丝列表 Fragment
 */
class UserFollowOrFansListFragment : AppFragment<AppActivity>(), StatusAction {

    private val mBinding by viewBinding<UserFollowOrFansListFragmentBinding>()
    private val mUserFollowListAdapter = UserFollowListAdapter(AdapterDelegate())
    private val mFollowViewModel by viewModels<FollowOrFansViewModel>()
    private val loadStateListener = loadStateListener(mUserFollowListAdapter) {
        mBinding.refreshLayout.finishRefresh()
    }

    override fun getLayoutId(): Int = R.layout.user_follow_or_fans_list_fragment

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
        val followState = fromJson<FollowType>(followStateJson) ?: FollowType.FOLLOW
        loadUserFollowList(userId, followState)
    }

    private fun loadUserFollowList(userId: String, followType: FollowType) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            mFollowViewModel.loadUserFollowOrFansListByState(userId, followType).collectLatest {
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
        fun newInstance(userId: String, followType: FollowType): UserFollowOrFansListFragment {
            val fragment = UserFollowOrFansListFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
                putString(IntentKey.OTHER, followType.toJson())
            }
            fragment.arguments = args
            return fragment
        }
    }
}