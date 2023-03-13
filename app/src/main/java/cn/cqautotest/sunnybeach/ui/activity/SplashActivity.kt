package cn.cqautotest.sunnybeach.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.view.View
import androidx.lifecycle.lifecycleScope
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.http.api.other.UserInfoApi
import cn.cqautotest.sunnybeach.http.model.HttpData
import cn.cqautotest.sunnybeach.ktx.checkToken
import cn.cqautotest.sunnybeach.other.AppConfig
import com.airbnb.lottie.LottieAnimationView
import com.blankj.utilcode.util.DeviceUtils
import com.dylanc.longan.context
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.hjq.http.EasyHttp
import com.hjq.http.listener.HttpCallback
import com.hjq.widget.view.SlantedTextView
import kotlinx.coroutines.launch
import java.util.*

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
        lifecycleScope.launch { checkToken() }
        if (AppConfig.isDebug()) {
            HomeActivity.start(context)
            return
        }
        if (DeviceUtils.isDevelopmentSettingsEnabled() || DeviceUtils.isAdbEnabled()) {
            toast("您的设备已开启调试模式，请注意安全")
        }
        // 设置动画监听
        lottieView?.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                lottieView?.removeAnimatorListener(this)
                HomeActivity.start(context)
                finish()
            }
        })
    }

    override fun initData() {
        debugView?.let {
            it.setText(AppConfig.getBuildType().uppercase(Locale.getDefault()))
            if (AppConfig.isDebug()) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.INVISIBLE
            }
        }

        if (true) {
            return
        }
        // 刷新用户信息
        EasyHttp.post(this)
            .api(UserInfoApi())
            .request(object : HttpCallback<HttpData<UserInfoApi.Bean?>>(this) {

                override fun onSucceed(data: HttpData<UserInfoApi.Bean?>) {

                }
            })
    }

    override fun createStatusBarConfig(): ImmersionBar {
        return super.createStatusBarConfig()
            // 隐藏状态栏和导航栏
            .hideBar(BarHide.FLAG_HIDE_BAR)
    }

    override fun onBackPressed() {
        // 禁用返回键
        //super.onBackPressed();
    }

    override fun initActivity() {
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

    override fun onDestroy() {
        // 因为修复了一个启动页被重复启动的问题，所以有可能 Activity 还没有初始化完成就已经销毁了
        // 所以如果需要在此处释放对象资源需要先对这个对象进行判空，否则可能会导致空指针异常
        super.onDestroy()
    }
}