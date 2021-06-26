package cn.cqautotest.sunnybeach.viewmodel.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.model.RequestHandler
import cn.cqautotest.sunnybeach.http.request.api.AppApi
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.other.AppConfig
import cn.cqautotest.sunnybeach.utils.*
import com.hjq.http.EasyConfig
import com.hjq.http.EasyHttp
import com.hjq.http.config.RequestServer
import com.hjq.http.lifecycle.ApplicationLifecycle
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.model.HttpMethod
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val api by lazy { ServiceCreator.create<AppApi>() }
    private var _appUpdateState = MutableLiveData<AppUpdateState>()
    val appUpdateState: LiveData<AppUpdateState> get() = _appUpdateState

    @JvmOverloads
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
                logByDebug(msg = "===> App更新数据获取成功")
                responseData.file =
                    File(
                        AppApplication.getInstance().filesDir.path,
                        "${responseData.versionName}.apk"
                    )
                _appUpdateState.value =
                    AppUpdateState(appUpdateInfo = responseData, isDataValid = true)
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
            // UMConfigure.setLogEnabled(AppConfig.isDebug())
            // // 客户端用户同意隐私政策后，正式初始化友盟+SDK
            // UMConfigure.init(
            //     getApplication(),
            //     "60c8883fe044530ff0a58a52",
            //     "XiaoMi",
            //     0,
            //     "7c6ef7a280231b605cc9d597471db50d"
            // )
            // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
            // 建议在宿主App的Application.onCreate函数中调用此函数。
            // MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

            // Push注册
            PushHelper.init()
        }
    }
}
