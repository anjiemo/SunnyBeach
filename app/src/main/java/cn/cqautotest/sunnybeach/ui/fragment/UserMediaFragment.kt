package cn.cqautotest.sunnybeach.ui.fragment

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserMediaFragmentBinding
import cn.cqautotest.sunnybeach.other.FollowState
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserArticleListFragment
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserFishListFragment
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserFollowListFragment
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserQaListFragment
import com.hjq.base.FragmentPagerAdapter

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户信息 Fragment
 */
class UserMediaFragment : AppFragment<AppActivity>() {

    private val mBinding by viewBinding<UserMediaFragmentBinding>()
    private val mPagerAdapter by lazy { FragmentPagerAdapter<AppFragment<AppActivity>>(this) }

    var mUserId = ""

    override fun getLayoutId(): Int = R.layout.user_media_fragment

    override fun initView() {
        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager
        viewPager.apply {
            adapter = mPagerAdapter
            mPagerAdapter.startUpdate(this)
        }
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initData() {
        mUserId = arguments?.getString(IntentKey.ID, "") ?: ""
        mPagerAdapter.apply {
            addFragment(UserFishListFragment.newInstance(mUserId), "动态")
            addFragment(UserArticleListFragment.newInstance(mUserId), "文章")
            addFragment(UserQaListFragment.newInstance(mUserId), "回答")
            addFragment(UserFollowListFragment.newInstance(mUserId, FollowState.FOLLOW), "关注")
            addFragment(UserFollowListFragment.newInstance(mUserId, FollowState.FANS), "粉丝")
            // addFragment(EmptyFragment.newInstance(), "分享")
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(userId: String): UserMediaFragment {
            val fragment = UserMediaFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}