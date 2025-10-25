package cn.cqautotest.sunnybeach.ui.fragment

import android.os.Bundle
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserMediaFragmentBinding
import cn.cqautotest.sunnybeach.ktx.clearTooltipText
import cn.cqautotest.sunnybeach.other.FollowType
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserArticleListFragment
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserFishListFragment
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserFollowOrFansListFragment
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserQaListFragment
import cn.cqautotest.sunnybeach.ui.fragment.user.media.UserShareListFragment
import com.hjq.base.FragmentPagerAdapter
import dev.androidbroadcast.vbpd.viewBinding

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户信息 Fragment
 */
class UserMediaFragment : AppFragment<AppActivity>() {

    private val mBinding by viewBinding(UserMediaFragmentBinding::bind)
    private val mPagerAdapter by lazy { FragmentPagerAdapter<AppFragment<AppActivity>>(this) }

    var mUserId = ""

    override fun getLayoutId(): Int = R.layout.user_media_fragment

    override fun initView() {
        mBinding.apply {
            viewPager.apply {
                adapter = mPagerAdapter
                mPagerAdapter.startUpdate(this)
            }
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    override fun initData() {
        mUserId = arguments?.getString(IntentKey.ID, "").orEmpty()
        mPagerAdapter.apply {
            addFragment(UserFishListFragment.newInstance(mUserId), "动态")
            addFragment(UserArticleListFragment.newInstance(mUserId), "文章")
            addFragment(UserQaListFragment.newInstance(mUserId), "回答")
            addFragment(UserFollowOrFansListFragment.newInstance(mUserId, FollowType.FOLLOW), "关注")
            addFragment(UserFollowOrFansListFragment.newInstance(mUserId, FollowType.FANS), "粉丝")
            addFragment(UserShareListFragment.newInstance(mUserId), "分享")
        }
        val tabLayout = mBinding.tabLayout
        tabLayout.clearTooltipText()
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