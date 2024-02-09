package cn.cqautotest.sunnybeach.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.db.CookieRoomDatabase
import cn.cqautotest.sunnybeach.db.CookieRoomDatabase.Companion.getDatabase
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.http.interceptor.accountInterceptor
import cn.cqautotest.sunnybeach.http.model.RequestHandler
import cn.cqautotest.sunnybeach.http.model.RequestServer
import cn.cqautotest.sunnybeach.ktx.resetConfiguration
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.other.AppConfig
import cn.cqautotest.sunnybeach.other.CrashHandler
import cn.cqautotest.sunnybeach.other.DebugLoggerTree
import cn.cqautotest.sunnybeach.other.SmartBallPulseFooter
import cn.cqautotest.sunnybeach.other.TitleBarStyle
import cn.cqautotest.sunnybeach.other.ToastStyle
import cn.cqautotest.sunnybeach.util.PushHelper
import cn.cqautotest.sunnybeach.work.CacheCleanupWorker
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import com.hjq.bar.TitleBar
import com.hjq.gson.factory.GsonFactory
import com.hjq.http.EasyConfig
import com.hjq.http.config.IRequestApi
import com.hjq.http.model.HttpHeaders
import com.hjq.http.model.HttpParams
import com.hjq.toast.ToastLogInterceptor
import com.hjq.toast.Toaster
import com.hjq.umeng.UmengClient
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.InputStream
import java.util.concurrent.TimeUnit

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2018/10/18
 *    desc   : 应用入口
 */
@HiltAndroidApp
class AppApplication : Application(), Configuration.Provider {

    @Log("启动耗时")
    override fun onCreate() {
        super.onCreate()
        mApp = this
        initSdk(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // 清理所有图片内存缓存
        GlideApp[this].onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // 根据手机内存剩余情况清理图片内存缓存
        GlideApp[this].onTrimMemory(level)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().also {
            if (AppConfig.isDebug()) {
                it.setMinimumLoggingLevel(android.util.Log.INFO)
            }
        }.build()

    override fun getResources(): Resources = super.getResources().apply {
        resetConfiguration()
    }

    companion object {

        private lateinit var mApp: AppApplication
        private lateinit var database: CookieRoomDatabase
        private const val WEATHER_API_TOKEN = "7xoSm4k7GIK8X8E1"

        fun getInstance() = mApp

        fun getDatabase() = database

        fun getWeatherApiToken(): String = WEATHER_API_TOKEN

        /**
         * 初始化一些第三方框架
         */
        fun initSdk(application: Application) {
            // 设置标题栏初始化器
            TitleBar.setDefaultStyle(TitleBarStyle())

            // 设置全局的 Header 构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context, _: RefreshLayout ->
                ClassicsHeader(context)
            }
            // 设置全局的 Footer 构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context, _: RefreshLayout ->
                SmartBallPulseFooter(context)
            }
            // 设置全局初始化器
            SmartRefreshLayout.setDefaultRefreshInitializer { _: Context, layout: RefreshLayout ->
                // 刷新头部是否跟随内容偏移
                layout.setEnableHeaderTranslationContent(true)
                    // 刷新尾部是否跟随内容偏移
                    .setEnableFooterTranslationContent(true)
                    // 加载更多是否跟随内容偏移
                    .setEnableFooterFollowWhenNoMoreData(true)
                    // 内容不满一页时是否可以上拉加载更多
                    .setEnableLoadMoreWhenContentNotFull(false)
                    // 仿苹果越界效果开关
                    .setEnableOverScrollDrag(false)
            }

            // 初始化吐司
            Toaster.init(application, ToastStyle())
            // 设置调试模式
            Toaster.setDebugMode(AppConfig.isDebug())
            // 设置 Toast 拦截器
            Toaster.setInterceptor(ToastLogInterceptor())

            // 本地异常捕捉
            CrashHandler.register(application)

            // 友盟统计、登录、分享 SDK
            UmengClient.init(application, AppConfig.isLogEnable())

            // Bugly 异常捕捉
            CrashReport.initCrashReport(application, AppConfig.getBuglyId(), AppConfig.isDebug())

            // Activity 栈管理初始化
            ActivityManager.getInstance().init(application)

            // MMKV 初始化
            MMKV.initialize(application)

            // 网络请求框架初始化
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(accountInterceptor)
                .build()

            EasyConfig.with(okHttpClient)
                // 是否打印日志
                .setLogEnabled(AppConfig.isLogEnable())
                // 设置服务器配置
                .setServer(RequestServer())
                // 设置请求处理策略
                .setHandler(RequestHandler(application))
                // 设置请求重试次数
                .setRetryCount(1)
                .setInterceptor { _: IRequestApi, _: HttpParams, headers: HttpHeaders ->
                    // 添加全局请求头
                    // headers.put("token", "66666666666")
                    headers.put("deviceOaid", UmengClient.getDeviceOaid())
                    headers.put("versionName", AppConfig.getVersionName())
                    headers.put("versionCode", AppConfig.getVersionCode().toString())
                }
                .into()

            // 设置 Json 解析容错监听
            GsonFactory.setJsonCallback { typeToken: TypeToken<*>, fieldName: String?, jsonToken: JsonToken ->
                // 上报到 Bugly 错误列表
                CrashReport.postCatchedException(IllegalArgumentException("类型解析异常：$typeToken#$fieldName，后台返回的类型为：$jsonToken"))
            }

            // 初始化日志打印
            if (AppConfig.isLogEnable()) {
                Timber.plant(DebugLoggerTree())
            }

            // MiPush 初始化
            PushHelper.init(application)

            // 注册网络状态变化监听
            val connectivityManager: ConnectivityManager? = ContextCompat.getSystemService(application, ConnectivityManager::class.java)
            if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onLost(network: Network) {
                        val topActivity: Activity? = ActivityManager.getInstance().getTopActivity()
                        if (topActivity !is LifecycleOwner) {
                            return
                        }
                        val lifecycleOwner: LifecycleOwner = topActivity
                        if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) {
                            return
                        }
                        Toaster.show(R.string.common_network_error)
                    }
                })
            }
            // 初始化 Room 数据库
            database = getDatabase(application)
            // 初始化 Glide 的 Cookie 管理
            Glide.get(application)
                .registry
                .replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))

            // Aria.init(application)

            initCacheCleanWork(application)
        }

        /**
         * 初始化 缓存清理工作
         *
         * @param application Application
         */
        private fun initCacheCleanWork(application: Application) {
            // 构造工作执行的约束条件
            val builder = Constraints.Builder() // 电池电量不低
                .setRequiresBatteryNotLow(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 设备处于空闲状态
                builder.setRequiresDeviceIdle(true)
            }
            val constraints = builder.build()
            // 定期工作请求（间隔三天工作一次）
            val workRequest = PeriodicWorkRequest.Builder(CacheCleanupWorker::class.java, 3, TimeUnit.DAYS) // 设置约束条件
                .setConstraints(constraints) // 符合约束条件后，延迟1分钟执行
                .setInitialDelay(0, TimeUnit.MINUTES)
                .build()
            val wm = WorkManager.getInstance(application)
            // 将工作加入队列中
            wm.enqueue(workRequest)
        }
    }
}