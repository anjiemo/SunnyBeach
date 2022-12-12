package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.FloatWindowAction
import cn.cqautotest.sunnybeach.action.OnBack2TopListener
import cn.cqautotest.sunnybeach.action.OnDoubleClickListener
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppFragment
import cn.cqautotest.sunnybeach.ktx.checkToken
import cn.cqautotest.sunnybeach.ktx.hideSupportActionBar
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.other.AppConfig
import cn.cqautotest.sunnybeach.other.DoubleClickHelper
import cn.cqautotest.sunnybeach.ui.adapter.HomeFragmentAdapter
import cn.cqautotest.sunnybeach.ui.adapter.NavigationAdapter
import cn.cqautotest.sunnybeach.ui.dialog.UpdateDialog
import cn.cqautotest.sunnybeach.ui.fragment.FishListFragment
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import com.gyf.immersionbar.ImmersionBar
import com.tencent.bugly.crashreport.CrashReport
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 首页界面
 */
@AndroidEntryPoint
class HomeActivity : AppActivity(), NavigationAdapter.OnNavigationListener, OnDoubleClickListener, FloatWindowAction {

    companion object {

        private const val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
        private const val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"

        @JvmOverloads
        fun start(context: Context, fragmentClass: Class<out AppFragment<*>?>? = FishListFragment::class.java) {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val viewPager2: ViewPager2? by lazy { findViewById(R.id.vp_home_pager2) }
    private val navigationView: RecyclerView? by lazy { findViewById(R.id.rv_home_navigation) }
    private var navigationAdapter: NavigationAdapter? = null
    private var pagerAdapter: HomeFragmentAdapter? = null
    private val updateDialog by lazy { UpdateDialog.Builder(this) }

    @Inject
    lateinit var mAppViewModel: AppViewModel

    override fun getLayoutId() = R.layout.home_activity

    override fun initView() {
        hideSupportActionBar()
        pagerAdapter = HomeFragmentAdapter(this).also { pagerAdapter = it }
        viewPager2?.let {
            it.isUserInputEnabled = false
            it.adapter = HomeFragmentAdapter(this).apply { pagerAdapter = this }
                .also { adapter -> it.offscreenPageLimit = adapter.itemCount }
        }
        navigationAdapter = NavigationAdapter(this).apply {
            addMenuItem(R.string.home_fish_pond_message, R.drawable.home_fish_pond_selector)
            addMenuItem(R.string.home_nav_qa, R.drawable.home_qa_selector)
            addMenuItem(R.string.home_nav_index, R.drawable.home_home_selector)
            addMenuItem(R.string.home_nav_course, R.drawable.home_course_selector)
            addMenuItem(R.string.home_nav_me, R.drawable.home_me_selector)
            setOnNavigationListener(this@HomeActivity)
            navigationView?.adapter = this
        }
    }

    private fun NavigationAdapter.addMenuItem(text: String, drawable: Drawable) {
        val menuItem = NavigationAdapter.MenuItem(text, drawable)
        addItem(menuItem)
    }

    private fun NavigationAdapter.addMenuItem(@StringRes text: Int, @DrawableRes resId: Int) {
        val drawable = ContextCompat.getDrawable(this@HomeActivity, resId)
        val menuItem = NavigationAdapter.MenuItem(getString(text), drawable)
        addItem(menuItem)
    }

    override fun initData() {
        onNewIntent(intent)

        toast("若有BUG，请及时反馈")

        lifecycleScope.launchWhenResumed { checkToken() }

        // 设置当前用户的阳光沙滩账号id，用于标识某位同学的APP发生了故障
        CrashReport.setUserId(UserManager.loadCurrUserId())
    }

    override fun initEvent() {
        viewPager2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                navigationAdapter?.setSelectedPosition(position)
                // 只有首页鱼塘才显示发布摸鱼按钮
                takeIf { position == 0 }?.let { showFloatWindow() }
                takeUnless { position == 0 }?.let { hideFloatWindow() }
            }
        })
        navigationAdapter?.setOnDoubleClickListener(this)
    }

    override fun initObserver() {
        mAppViewModel.checkAppUpdate().observe(this) { result ->
            val appUpdateInfo = result.getOrNull() ?: return@observe
            onlyCheckOrUpdate(appUpdateInfo)
        }
    }

    fun onlyCheckOrUpdate(appUpdateInfo: AppUpdateInfo) {
        // 是否需要强制更新（当前版本低于最低版本，强制更新）
        val minVersionCode = appUpdateInfo.minVersionCode
        val needForceUpdate = AppConfig.getVersionCode() < minVersionCode
        takeIf { needForceUpdate }?.let { showUpdateDialog(appUpdateInfo, true) }?.also { return }
        // 当前版本是否低于最新版本
        val lowerThanLatest = AppConfig.getVersionCode() < appUpdateInfo.versionCode
        takeIf { lowerThanLatest }?.let { showUpdateDialog(appUpdateInfo, appUpdateInfo.forceUpdate) }
    }

    private fun showUpdateDialog(appUpdateInfo: AppUpdateInfo, forceUpdateApp: Boolean) {
        updateDialog.setFileMd5(appUpdateInfo.apkHash)
            .setDownloadUrl(appUpdateInfo.url)
            .setForceUpdate(forceUpdateApp)
            .setUpdateLog(appUpdateInfo.updateLog)
            .setVersionName(appUpdateInfo.versionName)
            .show()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        pagerAdapter?.let {
            switchFragment(it.getFragmentIndex(getSerializable(INTENT_KEY_IN_FRAGMENT_CLASS)))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewPager2?.let {
            // 保存当前 Fragment 索引位置
            outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, it.currentItem)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX))
    }

    private fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }
        when (fragmentIndex) {
            0, 1, 2, 3, 4 -> {
                viewPager2?.currentItem = fragmentIndex
                navigationAdapter?.setSelectedPosition(fragmentIndex)
            }
        }
    }

    override fun onDoubleClick(v: View, position: Int) {
        // 如果当前显示的 Fragment 是可以回到顶部的，则调用回到顶部的方法
        pagerAdapter?.getItem(position)?.let { (it as? OnBack2TopListener)?.onBack2Top() }
    }

    /**
     * [NavigationAdapter.OnNavigationListener]
     */
    override fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            0, 1, 2, 3, 4 -> {
                viewPager2?.currentItem = position
                true
            }
            else -> false
        }
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig() // 指定导航栏背景颜色
            .navigationBarColor(R.color.white)
    }

    override fun onBackPressed() {
        // 退出 App 前，先回到首页 Fragment，防止用户误触
        viewPager2?.currentItem?.takeUnless { it == 0 }?.let { return switchFragment(0) }

        if (!DoubleClickHelper.isOnDoubleClick()) {
            toast(R.string.home_exit_hint)
            return
        }

        // 移动到上一个任务栈，避免侧滑引起的不良反应
        moveTaskToBack(false)
        postDelayed({
            // 进行内存优化，销毁掉所有的界面
            ActivityManager.getInstance().finishAllActivities()
        }, 300)
    }

    /**
     * NB! Please keep this method, although it does not appear to do anything, it is important and necessary to keep it.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager2?.adapter = null
        navigationView?.adapter = null
        navigationAdapter?.setOnNavigationListener(null)
    }

    override fun isStatusBarDarkFont(): Boolean {
        return true
    }
}