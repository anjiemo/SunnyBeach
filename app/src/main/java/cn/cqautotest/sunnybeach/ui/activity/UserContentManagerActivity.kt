package cn.cqautotest.sunnybeach.ui.activity

import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.UserContentManagerActivityBinding
import cn.cqautotest.sunnybeach.ktx.clearTooltipText
import cn.cqautotest.sunnybeach.ui.fragment.user.content.UserArticleListManagerFragment
import com.hjq.base.FragmentPagerAdapter

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/11/27
 * desc   : 用户内容管理 Activity
 */
class UserContentManagerActivity : AppActivity() {

    private val mBinding by viewBinding(UserContentManagerActivityBinding::bind)
    private val mPagerAdapter by lazy { FragmentPagerAdapter<AppFragment<AppActivity>>(this) }

    override fun getLayoutId(): Int = R.layout.user_content_manager_activity

    override fun initView() {
        mBinding.apply {
            viewPager.apply {
                adapter = mPagerAdapter
                mPagerAdapter.startUpdate(this)
                tabLayout.setupWithViewPager(this)
            }
        }
    }

    override fun initData() {
        mPagerAdapter.apply {
            addFragment(UserArticleListManagerFragment.newInstance(), "文章")
        }
        mBinding.apply {
            tabLayout.clearTooltipText()
        }
    }
}