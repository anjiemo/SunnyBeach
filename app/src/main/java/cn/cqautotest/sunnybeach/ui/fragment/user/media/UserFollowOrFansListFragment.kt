package cn.cqautotest.sunnybeach.ui.fragment.user.media

import android.os.Bundle
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.PagingFragment
import cn.cqautotest.sunnybeach.databinding.UserFollowOrFansListFragmentBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.snapshotList
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.other.FollowType
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.UserFollowListAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.FollowOrFansViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户关注/粉丝列表 Fragment
 */
class UserFollowOrFansListFragment : PagingFragment<AppActivity>() {

    private val mBinding by viewBinding<UserFollowOrFansListFragmentBinding>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mUserFollowListAdapter = UserFollowListAdapter(mAdapterDelegate)
    private val mFollowViewModel by viewModels<FollowOrFansViewModel>()

    override fun getPagingAdapter() = mUserFollowListAdapter

    override fun getLayoutId(): Int = R.layout.user_follow_or_fans_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
    }

    override suspend fun loadListData() {
        val userId = arguments?.getString(IntentKey.ID, "").orEmpty()
        val followStateJson = arguments?.getString(IntentKey.OTHER)
        val followType = fromJson<FollowType>(followStateJson) ?: FollowType.FOLLOW
        mFollowViewModel.loadUserFollowOrFansListByState(userId, followType).collectLatest {
            mUserFollowListAdapter.submitData(it)
        }
    }

    override fun initEvent() {
        super.initEvent()
        // 跳转到用户详情界面
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mUserFollowListAdapter.snapshotList[position]?.let { ViewUserActivity.start(requireContext(), it.userId) }
        }
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