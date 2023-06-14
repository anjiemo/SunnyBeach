package cn.cqautotest.sunnybeach.ui.activity

import android.content.Intent
import android.net.Uri
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.databinding.VipActivityBinding
import cn.cqautotest.sunnybeach.ktx.clearTooltipText
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import cn.cqautotest.sunnybeach.ui.fragment.VipIntroFragment
import cn.cqautotest.sunnybeach.ui.fragment.VipListFragment
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_TAOBAO_SHOP_VIP
import cn.cqautotest.sunnybeach.util.UmengReportKey
import com.blankj.utilcode.util.AppUtils
import com.hjq.bar.TitleBar
import com.hjq.base.FragmentPagerAdapter
import com.umeng.analytics.MobclickAgent

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
        mFragmentAdapter = FragmentPagerAdapter(this)
        mFragmentAdapter.addFragment(VipIntroFragment.newInstance(), "特权介绍")
        mFragmentAdapter.addFragment(VipListFragment.newInstance(), "贵宾席")
        mBinding.viewPager.adapter = mFragmentAdapter
        mBinding.tabLayout.clearTooltipText()
    }

    override fun onRightClick(titleBar: TitleBar) {
        MessageDialog.Builder(this)
            .setTitle("系统消息")
            .setMessage("即将打开淘宝店铺，请确认")
            .setConfirm("立即打开")
            .setCancel("我再想想")
            .setCanceledOnTouchOutside(false)
            .setListener {
                MobclickAgent.onEvent(this, UmengReportKey.BUY_VIP)
                tryOpenTaobaoApp()
            }
            .show()
    }

    /**
     * 尝试打开淘宝 APP
     */
    private fun tryOpenTaobaoApp() {
        val installTaobaoApp = AppUtils.isAppInstalled("com.taobao.taobao")
        takeIf { installTaobaoApp }?.let { openTaobaoApp() } ?: toast("您的设备似乎没有安装淘宝APP哦~")
    }

    /**
     * 打开淘宝 APP
     */
    private fun openTaobaoApp() {
        val uri = Uri.parse(SUNNY_BEACH_TAOBAO_SHOP_VIP)
        startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
            setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun isStatusBarDarkFont() = true

    override fun onDestroy() {
        super.onDestroy()
        mBinding.viewPager.adapter = null
    }
}