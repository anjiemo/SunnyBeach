package com.example.blogsystem.viewmodel.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.example.blogsystem.R
import com.example.blogsystem.base.App
import com.example.blogsystem.model.AppUpdateInfo
import com.example.blogsystem.network.RequestHandler
import com.example.blogsystem.network.ServiceCreator
import com.example.blogsystem.network.api.AppApi
import com.example.blogsystem.utils.*
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.config.RequestServer
import com.hjq.http.lifecycle.ApplicationLifecycle
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.model.HttpMethod
import com.tencent.mmkv.MMKV
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class AppViewModel(application: Application) : AndroidViewModel(application),
    NetworkUtils.OnNetworkStatusChangedListener {

    private val api by lazy { ServiceCreator.create<AppApi>() }
    private var _appUpdateState = MutableLiveData<AppUpdateState>()
    val appUpdateState: LiveData<AppUpdateState> get() = _appUpdateState

    fun checkAppVersionUpdate(url: String? = APP_INFO_URL) = viewModelScope.launch {
        url ?: return@launch
        val available = withContext(Dispatchers.IO) { NetworkUtils.isAvailable() }
        if (available.not()) return@launch
        runCatching {
            api.checkAppUpdate(url)
        }.onSuccess { response ->
            val responseData = response.data
            if (DEFAULT_HTTP_OK_CODE == response.code) {
                // App检查更新成功
                val currentVersion = AppConfig.getVersionCode()
                logByDebug(msg = "===> currentVersion:$currentVersion")
                // 如果当前版本小于服务器上的版本,则更新
                if (currentVersion < responseData.versionCode) {
                    logByDebug(msg = "===> 可以下载更新")
                    responseData.file =
                        File(App.get().filesDir.path, "${responseData.versionName}.apk")
                    _appUpdateState.value =
                        AppUpdateState(appUpdateInfo = responseData, isDataValid = true)
                }
            } else {
                // 服务器错误，App检查更新失败
                _appUpdateState.value =
                    AppUpdateState(checkUpdateError = R.string.check_update_error)
            }
        }.onFailure {
            // 网络错误，App检查更新失败
            _appUpdateState.value =
                AppUpdateState(networkError = R.string.network_error)
        }
    }

    fun downloadApk(
        appUpdateInfo: AppUpdateInfo,
        onStart: (file: File?) -> Unit = {},
        onProgress: (file: File?, progress: Int) -> Unit = { _, _ -> },
        onComplete: (file: File?) -> Unit = {},
        onError: (file: File?, e: Exception?) -> Unit = { _, _ -> },
        onEnd: (file: File?, appUpdateInfo: AppUpdateInfo) -> Unit = { _, _ -> }
    ) = downloadApk(
        HttpMethod.GET,
        appUpdateInfo.file,
        appUpdateInfo.url,
        appUpdateInfo.apkHash,
        appUpdateInfo.forceUpdate,
        appUpdateInfo,
        onStart,
        onProgress,
        onComplete,
        onError,
        onEnd
    )

    fun downloadApk(
        method: HttpMethod = HttpMethod.GET,
        file: File?,
        url: String?,
        apkHash: String?,
        forceUpdate: Boolean = false,
        appUpdateInfo: AppUpdateInfo,
        onStart: (file: File?) -> Unit = {},
        onProgress: (file: File?, progress: Int) -> Unit = { _, _ -> },
        onComplete: (file: File?) -> Unit = {},
        onError: (file: File?, e: Exception?) -> Unit = { _, _ -> },
        onEnd: (file: File?, appUpdateInfo: AppUpdateInfo) -> Unit = { _, _ -> }
    ) =
        EasyHttp.download(ApplicationLifecycle())
            .tag(TAG)
            .method(method)
            .file(file)
            .url(url)
            .apply {
                apkHash?.let {
                    md5(it)
                }
            }
            .listener(object : OnDownloadListener {
                override fun onStart(file: File?) {
                    onStart(file)
                }

                override fun onProgress(file: File?, progress: Int) {
                    onProgress.invoke(file, progress)
                }

                override fun onComplete(file: File?) {
                    onComplete.invoke(file)
                }

                override fun onError(file: File?, e: Exception?) {
                    onError.invoke(file, e)
                    e?.printStackTrace()
                }

                override fun onEnd(file: File?) {
                    onEnd.invoke(file, appUpdateInfo)
                }
            })

    fun initSDK() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            // MMKV初始化
            MMKV.initialize(getApplication())
            // 在此初始化其它依赖库
            Glide.get(getApplication())
                .registry
                .replace(
                    GlideUrl::class.java,
                    InputStream::class.java,
                    OkHttpUrlLoader.Factory(ServiceCreator.client)
                )
            UMConfigure.setLogEnabled(AppConfig.isDebug())
            // 客户端用户同意隐私政策后，正式初始化友盟+SDK
            UMConfigure.init(
                getApplication(),
                "60c8883fe044530ff0a58a52",
                "XiaoMi",
                0,
                "7c6ef7a280231b605cc9d597471db50d"
            )
            // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
            // 建议在宿主App的Application.onCreate函数中调用此函数。
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

            // Push注册
            PushHelper.init()

            // EasyHttp
            EasyConfig.with(ServiceCreator.client)
                // 是否打印日志
                .setLogEnabled(AppConfig.isDebug())
                // 设置服务器配置
                .setServer(RequestServer(BASE_URL))
                // 设置请求处理策略
                .setHandler(RequestHandler())
                // 设置请求重试次数
                .setRetryCount(3)
                // 启用配置
                .into()

            // 网络变化监听
            NetworkUtils.registerNetworkStatusChangedListener(this@AppViewModel)
        }
    }

    override fun onDisconnected() {
        // 只有app处于前台的时候才提示用户网络已断开，优化用户体验
        val appIsForeground = ActivityManager.appIsForeground()
        if (appIsForeground.not()) return
        simpleToast("网络已断开链接")
    }

    override fun onConnected(networkType: NetworkUtils.NetworkType?) {

    }
}
