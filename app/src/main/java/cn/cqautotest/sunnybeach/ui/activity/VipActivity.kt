package cn.cqautotest.sunnybeach.ui.activity

import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.VipActivityBinding
import cn.cqautotest.sunnybeach.ktx.clearTooltipText
import cn.cqautotest.sunnybeach.ui.fragment.VipIntroFragment
import cn.cqautotest.sunnybeach.ui.fragment.VipListFragment
import com.hjq.base.FragmentPagerAdapter

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/02/01
 * desc   : Vip界面
 */
class VipActivity : AppActivity() {

    private val mBinding: VipActivityBinding by viewBinding()
    private lateinit var mFragmentAdapter: FragmentPagerAdapter<AppFragment<*>>

    override fun getLayoutId(): Int = R.layout.vip_activity

    override fun initView() {
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)
    }

    override fun initData() {
        mFragmentAdapter = FragmentPagerAdapter<AppFragment<*>>(this)
        mFragmentAdapter.addFragment(VipIntroFragment.newInstance(), "特权介绍")
        mFragmentAdapter.addFragment(VipListFragment.newInstance(), "贵宾席")
        mBinding.viewPager.adapter = mFragmentAdapter
        mBinding.tabLayout.clearTooltipText()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.viewPager.adapter = null
    }
}