package cn.cqautotest.sunnybeach.app

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.lifecycle.lifecycleScope
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.action.TitleBarAction
import cn.cqautotest.sunnybeach.action.ToastAction
import cn.cqautotest.sunnybeach.http.model.HttpData
import cn.cqautotest.sunnybeach.ktx.checkToken
import cn.cqautotest.sunnybeach.ui.dialog.WaitDialog
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.TitleBar
import com.hjq.base.BaseActivity
import com.hjq.base.BaseDialog
import com.hjq.http.listener.OnHttpListener
import okhttp3.Call

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : Activity 业务基类
 */
abstract class AppActivity : BaseActivity(), Init,
    ToastAction, TitleBarAction, OnHttpListener<Any> {

    /** 标题栏对象 */
    private var titleBar: TitleBar? = null

    /** 状态栏沉浸 */
    private var immersionBar: ImmersionBar? = null

    /** 加载对话框 */
    private var dialog: BaseDialog? = null

    /** 对话框数量 */
    private var dialogCount: Int = 0

    /**
     * 初始化事件监听和观察者
     */
    override fun initActivity() {
        super.initActivity()
        initEvent()
        initObserver()
    }

    override fun initEvent() {}

    override fun initObserver() {}

    /**
     * 当前加载对话框是否在显示中
     */
    open fun isShowDialog(): Boolean {
        return dialog != null && dialog!!.isShowing
    }

    /**
     * 显示加载对话框
     */
    open fun showDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        dialogCount++
        postDelayed(Runnable {
            if ((dialogCount <= 0) || isFinishing || isDestroyed) {
                return@Runnable
            }
            if (dialog == null) {
                dialog = WaitDialog.Builder(this)
                    .setCancelable(false)
                    .create()
            }
            if (!dialog!!.isShowing) {
                dialog!!.show()
            }
        }, 300)
    }

    /**
     * 隐藏加载对话框
     */
    open fun hideDialog() {
        if (isFinishing || isDestroyed) {
            return
        }
        if (dialogCount > 0) {
            dialogCount--
        }
        if ((dialogCount != 0) || (dialog == null) || !dialog!!.isShowing) {
            return
        }
        dialog?.dismiss()
    }

    override fun initLayout() {
        super.initLayout()

        val titleBar = getTitleBar()
        titleBar?.setOnTitleBarListener(this)

        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            getStatusBarConfig().init()

            // 设置标题栏沉浸
            if (titleBar != null) {
                ImmersionBar.setTitleBar(this, titleBar)
            }
        }
    }

    /**
     * 是否使用沉浸式状态栏
     */
    protected open fun isStatusBarEnabled() = true

    /**
     * 状态栏字体深色模式
     */
    open fun isStatusBarDarkFont(): Boolean {
        return false
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    open fun getStatusBarConfig(): ImmersionBar {
        if (immersionBar == null) {
            immersionBar = createStatusBarConfig()
        }
        return immersionBar!!
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected open fun createStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this) // 默认状态栏字体颜色为黑色
            .statusBarDarkFont(isStatusBarDarkFont()) // 指定导航栏背景颜色
            .navigationBarColor(R.color.white) // 状态栏字体和导航栏内容自动变色，必须指定状态栏颜色和导航栏颜色才可以自动变色
            .autoDarkModeEnable(true, 0.2f)
    }

    /**
     * 设置标题栏的标题
     */
    override fun setTitle(@StringRes id: Int) {
        title = getString(id)
    }

    /**
     * 设置标题栏的标题
     */
    override fun setTitle(title: CharSequence?) {
        super<BaseActivity>.setTitle(title)
        getTitleBar()?.title = title
    }

    override fun getTitleBar(): TitleBar? {
        if (titleBar == null) {
            titleBar = obtainTitleBar(getContentView())
        }
        return titleBar
    }

    override fun onLeftClick(titleBar: TitleBar) {
        onBackPressed()
    }

    /**
     * [OnHttpListener]
     */
    override fun onStart(call: Call) {
        showDialog()
    }

    override fun onSucceed(result: Any) {
        if (result is HttpData<*>) {
            toast(result.getMessage())
        }
    }

    override fun onFail(e: Exception) {
        toast(e.message)
    }

    override fun onEnd(call: Call) {
        hideDialog()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        // if (!AppConfig.isDebug() && DeviceUtils.isDeviceRooted()) {
        //     toast("请勿在root设备使用本App")
        //     ActivityManager.getInstance().finishAllActivities()
        //     return
        // }
        // if (!AppConfig.isDebug() && DeviceUtils.isEmulator()) {
        //     toast("请勿在模拟器上使用本App");
        //     ActivityManager.getInstance().finishAllActivities()
        //     return
        // }
        lifecycleScope.launchWhenCreated { checkToken() }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isShowDialog()) {
            hideDialog()
        }
        dialog = null
    }
}