package cn.cqautotest.sunnybeach.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.webkit.CookieManager
import android.widget.TextClock
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.databinding.SettingActivityBinding
import cn.cqautotest.sunnybeach.db.SobCacheManager
import cn.cqautotest.sunnybeach.http.api.other.LogoutApi
import cn.cqautotest.sunnybeach.http.model.HttpData
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.manager.AppManager
import cn.cqautotest.sunnybeach.manager.CacheDataManager
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.other.AppConfig
import cn.cqautotest.sunnybeach.other.RoundRectDrawable
import cn.cqautotest.sunnybeach.ui.dialog.MenuDialog
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import cn.cqautotest.sunnybeach.ui.dialog.SafeDialog
import cn.cqautotest.sunnybeach.ui.dialog.SelectDialog
import cn.cqautotest.sunnybeach.ui.dialog.UpdateDialog
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import com.dylanc.longan.context
import com.flyjingfish.android_aop_core.annotations.SingleClick
import com.hjq.base.action.AnimAction
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.hjq.widget.layout.SettingBar
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.utils.DisplayUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/03/01
 *    desc   : 设置界面
 */
@AndroidEntryPoint
class SettingActivity : AppActivity() {

    private val mBinding: SettingActivityBinding by viewBinding()
    private val languageView: SettingBar? by lazy { findViewById(R.id.sb_setting_language) }
    private val cleanCacheView: SettingBar? by lazy { findViewById(R.id.sb_setting_cache) }

    @Inject
    lateinit var mAppViewModel: AppViewModel
    private var isAutoCheckAppVersion = true
    private var mAppVersionLiveData = MutableLiveData<AppUpdateInfo?>()

    private val mUserViewModel by viewModels<UserViewModel>()

    override fun getLayoutId(): Int = R.layout.setting_activity

    override fun initView() {
        setOnClickListener(
            R.id.sb_setting_language,
            R.id.sb_setting_update,
            R.id.sb_setting_agreement,
            R.id.sb_setting_me_pay,
            R.id.sb_setting_about,
            R.id.sb_setting_cache,
            R.id.sb_setting_exit
        )
        mBinding.apply {
            sbSettingExit.isVisible = UserManager.isLogin()
            tvCurrentVersion.text = getString(R.string.current_version, AppConfig.getVersionName())
        }
        updateFloatWindowSwitchState()
    }

    override fun initData() {
        // 获取应用缓存大小
        cleanCacheView?.setRightText(CacheDataManager.getTotalCacheSize(this))
        languageView?.setRightText("简体中文")
        // 检查更新
        mAppViewModel.checkAppUpdate().observe(this) {
            isAutoCheckAppVersion = true
            mAppVersionLiveData.value = it.getOrNull()
        }
    }

    override fun initEvent() {
        mBinding.apply {
            // 设置时间悬浮窗切换按钮的监听
            sbSettingTimeFloatWindowSwitch.setFixOnClickListener {
                toggleTimeFloatWindow(sbSettingTimeFloatWindowSwitch.isChecked())
            }
            sbSettingCache.setOnLongClickListener {
                fun Int.isOpenAutoCleanCache() = this == 0
                fun Boolean.selectIndex() = if (this) 0 else 1
                // 选择清理模式
                SelectDialog.Builder(context)
                    .setTitle("空闲时自动清理")
                    .setList("开", "关")
                    // 设置单选模式
                    .setSingleSelect()
                    // 设置默认选中
                    .setSelect(AppManager.isAutoCleanCache().selectIndex())
                    .setListener { _, data ->
                        // 单选
                        val keys = data.keys
                        if (keys.size == 1) {
                            val key = keys.toList()[0]
                            AppManager.setAutoCleanCache(key.isOpenAutoCleanCache())
                        }
                    }
                    .show()
                true
            }
        }
    }

    override fun initObserver() {
        mAppVersionLiveData.observe(this) { appUpdateInfo ->
            hideUpdateIcon()
            appUpdateInfo ?: run {
                takeUnless { isAutoCheckAppVersion }?.let { toast(R.string.check_update_error) }
                return@observe
            }
            // 是否需要强制更新（当前版本低于最低版本，强制更新）
            val needForceUpdate = AppConfig.getVersionCode() < appUpdateInfo.minVersionCode
            if (needForceUpdate) {
                showUpdateIcon()
                showAppUpdateDialog(appUpdateInfo, true)
                return@observe
            }
            // 当前版本是否低于最新版本
            val lowerThanLatest = AppConfig.getVersionCode() < appUpdateInfo.versionCode
            if (lowerThanLatest) {
                showUpdateIcon()
                showAppUpdateDialog(appUpdateInfo, appUpdateInfo.forceUpdate)
            } else {
                takeUnless { isAutoCheckAppVersion }?.let { toast(R.string.current_version_is_up_to_date) }
            }
        }
    }

    private fun showAppUpdateDialog(appUpdateInfo: AppUpdateInfo, forceUpdateApp: Boolean) {
        UpdateDialog.Builder(this)
            .setFileMd5(appUpdateInfo.apkHash)
            .setDownloadUrl(appUpdateInfo.url)
            .setForceUpdate(forceUpdateApp)
            .setUpdateLog(appUpdateInfo.updateLog)
            .setVersionName(appUpdateInfo.versionName)
            .show()
    }

    /**
     * 隐藏更新提示图标
     */
    private fun hideUpdateIcon() {
        mBinding.tvSettingUpdate.isVisible = false
    }

    /**
     * 显示更新提示图标
     */
    private fun showUpdateIcon() {
        mBinding.tvSettingUpdate.isVisible = true
    }

    override fun onClick(view: View) {
        onSingClick(view)
    }

    @SingleClick
    private fun onSingClick(view: View) {
        when (view.id) {
            R.id.sb_setting_language -> {
                // 底部选择框
                MenuDialog.Builder(this) // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(R.string.setting_language_simple, R.string.setting_language_complex)
                    .setListener { _, _, data ->
                        languageView?.setRightText(data.toString())
                        BrowserActivity.start(this, "https://github.com/getActivity/MultiLanguages")
                    }
                    .setGravity(Gravity.BOTTOM)
                    .setAnimStyle(AnimAction.ANIM_BOTTOM)
                    .show()
            }
            R.id.sb_setting_update -> {
                // 检查更新
                mAppViewModel.checkAppUpdate().observe(this) {
                    isAutoCheckAppVersion = false
                    mAppVersionLiveData.value = it.getOrNull()
                }
            }
            R.id.sb_setting_phone -> {
                SafeDialog.Builder(this)
                    .setListener { _, _, code -> PhoneResetActivity.start(this, code) }
                    .show()
            }
            R.id.sb_setting_agreement -> {
                BrowserActivity.start(this, "https://github.com/anjiemo/SunnyBeach")
            }
            R.id.sb_setting_me_pay -> {
                MessageDialog.Builder(this)
                    .setTitle("捐赠")
                    .setMessage("如果你觉得这个开源项目很棒，希望它能更好地坚持开发下去，可否愿意花一点点钱（推荐 10.24 元）作为对于开发者的激励")
                    .setConfirm("支付宝")
                    .setCancel(null) //.setAutoDismiss(false)
                    .setListener {
                        val PAY_QR_URL =
                            "http://r.photo.store.qq.com/psb?/V11f9eYN22gzYb/1l8Fxuv4qOVPY4IVk8e0gnwGAUevkQEIdTQkY7u1fbo!/r/dFsBAAAAAAAA"
                        BrowserActivity.start(this, PAY_QR_URL)
                        toast("【阳光沙滩】 因为有你的支持而能够不断更新、完善，非常感谢支持！")
                        postDelayed({
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2FFKX051528LQJA7OWAZ76A4%3F_s%3Dweb-other")
                                )
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                e.printStackTrace()
                                toast("打开支付宝失败，你可能还没有安装支付宝客户端")
                            }
                        }, 2000)
                    }
                    .show()
            }
            R.id.sb_setting_about -> {
                startActivity<AboutActivity>()
            }
            R.id.sb_setting_cache -> {
                mAppViewModel.clearCacheMemory().observe(this) {
                    val totalCacheSize = it.getOrNull() ?: return@observe
                    mBinding.sbSettingCache.setRightText(totalCacheSize)
                }
            }
            R.id.sb_setting_exit -> {
                MessageDialog.Builder(this)
                    .setTitle("退出账号")
                    .setMessage("确定退出当前账号")
                    .setConfirm("确定")
                    .setCancel("取消")
                    .setListener { logout() }.show()
                if (true) {
                    return
                }
                // 退出登录
                EasyHttp.post(this)
                    .api(LogoutApi())
                    .request(object : HttpCallback<HttpData<Void?>>(this) {
                        override fun onSucceed(data: HttpData<Void?>) {
                            LoginActivity.start(
                                context,
                                UserManager.getCurrLoginAccount(),
                                UserManager.getCurrLoginAccountPassword()
                            )
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(LoginActivity::class.java)
                        }
                    })
            }
        }
    }

    private fun logout() {
        // 清除 WebView 的 Cookie
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        val database = AppApplication.getDatabase()
        val cookieDao = database.cookieDao()
        lifecycleScope.launchWhenCreated {
            // 清除App本地缓存的 Cookie（必须在非主线程操作）
            withContext(Dispatchers.IO) { cookieDao.clearCookies() }
        }
        // 退出账号并清除用户基本信息数据
        mUserViewModel.logout().observe(this) {
            // 清除用户信息
            UserManager.exitUserAccount()
            SobCacheManager.onAccountLoginOut()
            HomeActivity.start(this)
            // 进行内存优化，销毁除首页之外的所有界面
            ActivityManager.getInstance().finishAllActivities(HomeActivity::class.java)
        }
    }

    private fun updateFloatWindowSwitchState() {
        mBinding.sbSettingTimeFloatWindowSwitch.setChecked(EasyFloat.isShow(TIME_FLOAT_WINDOW_FLAG))
    }

    private fun toggleTimeFloatWindow(checked: Boolean) {
        if (checked) {
            showTimeFloatWindow()
        } else {
            hideTimeFloatWindow()
        }
    }

    private fun hideTimeFloatWindow() {
        EasyFloat.dismiss(TIME_FLOAT_WINDOW_FLAG, true)
    }

    private fun showTimeFloatWindow() {
        EasyFloat.with(this)
            .setLayout(TextClock(this).apply {
                format12Hour = "HH:mm:ss"
                format24Hour = "HH:mm:ss"
                background = RoundRectDrawable(4.dp, "#F1F3F4".toColorInt())
                updatePadding(6.dp, 4.dp, 6.dp, 4.dp)
            })
            // 设置浮窗显示类型，默认只在当前Activity显示，可选一直显示、仅前台显示
            .setShowPattern(ShowPattern.ALL_TIME)
            .setTag(TIME_FLOAT_WINDOW_FLAG)
            // 设置吸附方式，共15种模式，详情参考SidePattern
            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
            // 设置浮窗的对齐方式和坐标偏移量
            .setGravity(gravity = Gravity.END or Gravity.TOP, offsetY = 100.dp)
            // 设置当布局大小变化后，整体view的位置对齐方式
            .setLayoutChangedGravity(Gravity.END)
            // 设置系统浮窗的有效显示高度（不包含虚拟导航栏的高度），基本用不到，除非有虚拟导航栏适配问题
            .setDisplayHeight { context -> DisplayUtils.rejectedNavHeight(context) }
            .registerCallback {
                createResult { isCreated, msg, view ->
                    mBinding.sbSettingTimeFloatWindowSwitch.setChecked(isCreated)
                }
            }
            .show()
    }

    override fun isStatusBarDarkFont() = false

    companion object {

        private const val TIME_FLOAT_WINDOW_FLAG = "TIME_FLOAT_WINDOW_FLAG"
    }
}