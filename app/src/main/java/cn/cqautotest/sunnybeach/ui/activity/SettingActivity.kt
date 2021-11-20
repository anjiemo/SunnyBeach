package cn.cqautotest.sunnybeach.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.webkit.CookieManager
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.SingleClick
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.databinding.SettingActivityBinding
import cn.cqautotest.sunnybeach.http.model.HttpData
import cn.cqautotest.sunnybeach.http.request.LogoutApi
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.manager.CacheDataManager
import cn.cqautotest.sunnybeach.manager.ThreadPoolManager
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.other.AppConfig
import cn.cqautotest.sunnybeach.ui.dialog.MenuDialog
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import cn.cqautotest.sunnybeach.ui.dialog.SafeDialog
import cn.cqautotest.sunnybeach.ui.dialog.UpdateDialog
import cn.cqautotest.sunnybeach.util.startActivity
import cn.cqautotest.sunnybeach.viewmodel.app.AppViewModel
import com.hjq.base.BaseDialog
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.hjq.widget.view.SwitchButton

/**
 * author : Android 轮子哥 & A Lonely Cat
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2019/03/01
 * desc   : 设置界面
 */
class SettingActivity : AppActivity(), SwitchButton.OnCheckedChangeListener {

    private val mBinding: SettingActivityBinding by viewBinding()
    private var mAppLiveData = MutableLiveData<AppUpdateInfo?>()
    private val mAppViewModel: AppViewModel = AppApplication.getAppViewModel()

    override fun getLayoutId(): Int = R.layout.setting_activity

    override fun initObserver() {
        mAppLiveData.observe(this) { appUpdateInfo ->
            hideUpdateIcon()
            appUpdateInfo ?: return@observe
            if (appUpdateInfo.versionCode <= AppConfig.getVersionCode()) {
                return@observe
            }
            showUpdateIcon()
            UpdateDialog.Builder(this)
                .setFileMd5(appUpdateInfo.apkHash)
                .setDownloadUrl(appUpdateInfo.url)
                .setForceUpdate(appUpdateInfo.forceUpdate)
                .setUpdateLog(appUpdateInfo.updateLog)
                .setVersionName(appUpdateInfo.versionName)
                .show()
        }
        mBinding.sbSettingCache.rightText = CacheDataManager.getTotalCacheSize(this)
    }

    override fun initView() {
        // 设置切换按钮的监听
        mBinding.sbSettingSwitch.setOnCheckedChangeListener(this)
        setOnClickListener(
            R.id.sb_setting_language,
            R.id.sb_setting_update,
            R.id.sb_setting_phone,
            R.id.sb_setting_password,
            R.id.sb_setting_agreement,
            R.id.sb_setting_me_pay,
            R.id.sb_setting_about,
            R.id.sb_setting_cache,
            R.id.sb_setting_auto,
            R.id.sb_setting_exit
        )
    }

    override fun initData() {
        // 获取应用缓存大小
        mBinding.sbSettingCache.rightText = CacheDataManager.getTotalCacheSize(this)
        mBinding.sbSettingLanguage.rightText = "简体中文"
        mBinding.sbSettingPhone.rightText = "181****1413"
        mBinding.sbSettingPassword.rightText = "密码强度较低"
        mBinding.sbSettingSwitch.isChecked = true
        // 检查更新
        mAppViewModel.checkAppUpdate().observe(this) {
            mAppLiveData.value = it.getOrNull()
        }
    }

    @SingleClick
    override fun onClick(view: View) {
        when (view.id) {
            R.id.sb_setting_language -> {
                // 底部选择框
                MenuDialog.Builder(this) // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setList(R.string.setting_language_simple, R.string.setting_language_complex)
                    .setListener(MenuDialog.OnListener { _: BaseDialog?, _: Int, string: String? ->
                        mBinding.sbSettingLanguage.rightText = string
                        BrowserActivity.start(
                            activity,
                            "https://github.com/getActivity/MultiLanguages"
                        )
                    })
                    .setGravity(Gravity.BOTTOM)
                    .setAnimStyle(BaseDialog.ANIM_BOTTOM)
                    .show()
            }
            R.id.sb_setting_update -> {
                // 检查更新
                mAppViewModel.checkAppUpdate().observe(this) {
                    mAppLiveData.value = it.getOrNull()
                }
            }
            R.id.sb_setting_phone -> {
                SafeDialog.Builder(this)
                    .setListener { _: BaseDialog?, _: String?, code: String? ->
                        PhoneResetActivity.start(
                            activity, code
                        )
                    }
                    .show()
            }
            R.id.sb_setting_password -> {
                SafeDialog.Builder(this)
                    .setListener { _: BaseDialog?, phone: String?, code: String? ->
                        PasswordResetActivity.start(
                            activity, phone, code
                        )
                    }
                    .show()
            }
            R.id.sb_setting_agreement -> {
                BrowserActivity.start(activity, "https://github.com/anjiemo/SunnyBeach")
            }
            R.id.sb_setting_me_pay -> {
                MessageDialog.Builder(activity)
                    .setTitle("捐赠")
                    .setMessage("如果你觉得这个开源项目很棒，希望它能更好地坚持开发下去，可否愿意花一点点钱（推荐 10.24 元）作为对于开发者的激励")
                    .setConfirm("支付宝")
                    .setCancel(null) //.setAutoDismiss(false)
                    .setListener {
                        val PAY_QR_URL =
                            "http://r.photo.store.qq.com/psb?/V11f9eYN22gzYb/1l8Fxuv4qOVPY4IVk8e0gnwGAUevkQEIdTQkY7u1fbo!/r/dFsBAAAAAAAA"
                        BrowserActivity.start(activity, PAY_QR_URL)
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
                                toast("打开支付宝失败，你可能还没有安装支付宝客户端")
                            }
                        }, 2000)
                    }
                    .show()
            }
            R.id.sb_setting_about -> {
                startActivity<AboutActivity>()
            }
            R.id.sb_setting_auto -> {
                // 自动登录
                val sbSettingSwitch = mBinding.sbSettingSwitch
                sbSettingSwitch.isChecked = !sbSettingSwitch.isChecked
            }
            R.id.sb_setting_cache -> {
                mAppViewModel.clearCacheMemory().observe(this) {
                    val totalCacheSize = it.getOrNull() ?: return@observe
                    mBinding.sbSettingCache.rightText = totalCacheSize
                }
            }
            R.id.sb_setting_exit -> {
                // 清除用户信息
                UserManager.exitUserAccount()
                // 清除 WebView 的 Cookie
                val cookieManager = CookieManager.getInstance()
                cookieManager.removeAllCookies(null)
                val database = AppApplication.getDatabase()
                val cookieDao = database.cookieDao()
                ThreadPoolManager.getInstance().execute {
                    // 清除App本地缓存的 Cookie（必须在非主线程操作）
                    cookieDao.clearCookies()
                }
                if (true) {
                    // TODO: 退出账号并清除用户基本信息数据
                    startActivity<LoginActivity>()
                    // 进行内存优化，销毁除登录页之外的所有界面
                    ActivityManager.getInstance().finishAllActivities(
                        LoginActivity::class.java
                    )
                    return
                }
                // 退出登录
                EasyHttp.post(this)
                    .api(LogoutApi())
                    .request(object : HttpCallback<HttpData<Void?>>(this) {
                        override fun onSucceed(data: HttpData<Void?>) {
                            startActivity<LoginActivity>()
                            // 进行内存优化，销毁除登录页之外的所有界面
                            ActivityManager.getInstance().finishAllActivities(
                                LoginActivity::class.java
                            )
                        }
                    })
            }
        }
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

    /**
     * [SwitchButton.OnCheckedChangeListener]
     */
    override fun onCheckedChanged(button: SwitchButton, checked: Boolean) {
        // TODO: 设置是否自动登录
    }

    override fun isStatusBarDarkFont(): Boolean {
        return false
    }
}