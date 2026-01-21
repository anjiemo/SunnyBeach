package cn.cqautotest.sunnybeach.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.deeplink.AppSchemeServiceDispatcher
import cn.cqautotest.sunnybeach.http.api.other.UserInfoApi
import cn.cqautotest.sunnybeach.http.model.HttpData
import cn.cqautotest.sunnybeach.ktx.checkToken
import cn.cqautotest.sunnybeach.other.AppConfig
import com.airbnb.lottie.LottieAnimationView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.Utils
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallbackProxy
import com.hjq.widget.view.SlantedTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 闪屏界面
 */
class SplashActivity : AppActivity() {

    private val lottieView: LottieAnimationView? by lazy { findViewById(R.id.lav_splash_lottie) }
    private val debugView: SlantedTextView? by lazy { findViewById(R.id.iv_splash_debug) }

    override fun getLayoutId(): Int = R.layout.splash_activity

    override fun initView() {
        CoroutineScope(Dispatchers.IO).launch { checkToken() }
        if (AppConfig.isDebug()) {
            nextPage()
            return
        }
        if (DeviceUtils.isDevelopmentSettingsEnabled() || DeviceUtils.isAdbEnabled()) {
            toast("您的设备已开启调试模式，请注意安全")
        }
        // 设置动画监听
        lottieView?.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                lottieView?.removeAnimatorListener(this)
                nextPage()
            }
        })
        // 延迟取消动画，避免动画执行时间过长
        lifecycleScope.launch {
            delay(900)
            lottieView?.cancelAnimation()
        }
    }

    private fun nextPage() {
        HomeActivity.start(this)
        finish()
    }

    override fun initData() {
        debugView?.let {
            it.setText(AppConfig.getBuildType().uppercase())
            it.isInvisible = AppConfig.isDebug().not()
        }

        if (true) {
            return
        }
        // 刷新用户信息
        EasyHttp.post(this)
            .api(UserInfoApi())
            .request(object : HttpCallbackProxy<HttpData<UserInfoApi.Bean?>>(this) {

                override fun onHttpSuccess(result: HttpData<UserInfoApi.Bean?>) {

                }
            })
    }

    override fun initEvent() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                // 禁用返回键
                // Nothing to do.
            }
        })
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            // 隐藏状态栏和导航栏
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }

    override fun initActivity() {
        // 处理 DeepLink
        handleAppDeepLink()
        // 问题及方案：https://www.cnblogs.com/net168/p/5722752.html
        // 如果当前 Activity 不是任务栈中的第一个 Activity
        if (!isTaskRoot) {
            val intent: Intent? = intent
            // 如果当前 Activity 是通过桌面图标启动进入的
            if (((intent != null) && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && (Intent.ACTION_MAIN == intent.action))) {
                // 对当前 Activity 执行销毁操作，避免重复实例化入口
                finish()
                return
            }
        }
        super.initActivity()
    }

    private fun handleAppDeepLink() {
        // 使用启动页的 intent
        val intent = intent
        ActivityUtils.addActivityLifecycleCallbacks(object : Utils.ActivityLifecycleCallbacks() {
            override fun onActivityStarted(activity: Activity) {
                if (activity !is HomeActivity) {
                    return
                }
                // 跳转到首页后，移除 Activity 生命周期回调，避免造成内存泄漏
                ActivityUtils.removeActivityLifecycleCallbacks(this)
                // 处理 DeepLink
                AppSchemeServiceDispatcher.dispatch(activity, intent)
            }
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 设置新的 Intent，确保 handleAppDeepLink 获取的是最新的 DeepLink 数据
        setIntent(intent)
        // 重新处理 DeepLink 逻辑，防止在 singleTop 模式下实例复用导致分发失败
        handleAppDeepLink()
    }

    override fun onDestroy() {
        // 因为修复了一个启动页被重复启动的问题，所以有可能 Activity 还没有初始化完成就已经销毁了
        // 所以如果需要在此处释放对象资源需要先对这个对象进行判空，否则可能会导致空指针异常
        super.onDestroy()
    }
}