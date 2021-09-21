package cn.cqautotest.sunnybeach.viewmodel.app

import android.app.Application
import androidx.lifecycle.*
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.AppApi
import cn.cqautotest.sunnybeach.model.AppUpdateInfo
import cn.cqautotest.sunnybeach.other.AppConfig
import cn.cqautotest.sunnybeach.util.APP_INFO_URL
import cn.cqautotest.sunnybeach.util.DEFAULT_HTTP_OK_CODE
import cn.cqautotest.sunnybeach.util.TAG
import cn.cqautotest.sunnybeach.util.logByDebug
import com.hjq.http.EasyHttp
import com.hjq.http.listener.OnDownloadListener
import com.hjq.http.model.HttpMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 应用的 ViewModel
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val mAppApi by lazy { ServiceCreator.create<AppApi>() }
    private var _appUpdateState = MutableLiveData<AppUpdateState>()
    val appUpdateState: LiveData<AppUpdateState> get() = _appUpdateState

    @JvmOverloads
    fun checkAppVersionUpdate(url: String = APP_INFO_URL) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    mAppApi.checkAppUpdate(url)
                }
            }.onSuccess { response ->
                val responseData = response.data
                if (DEFAULT_HTTP_OK_CODE == response.code) {
                    // App检查更新成功
                    val currentVersion = AppConfig.getVersionCode()
                    logByDebug(msg = "===> currentVersion:$currentVersion")
                    logByDebug(msg = "===> App更新数据获取成功")
                    responseData.file =
                        File(
                            getApplication<Application>().filesDir.path,
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
                it.printStackTrace()
                // 网络错误，App检查更新失败
                _appUpdateState.value =
                    AppUpdateState(networkError = R.string.network_error)
            }
        }
    }

    fun downloadApk(
        lifecycleOwner: LifecycleOwner,
        appUpdateInfo: AppUpdateInfo,
        onStart: (file: File?) -> Unit = {},
        onProgress: (file: File?, progress: Int) -> Unit = { _, _ -> },
        onComplete: (file: File?) -> Unit = {},
        onError: (file: File?, e: Exception?) -> Unit = { _, _ -> },
        onEnd: (file: File?, appUpdateInfo: AppUpdateInfo) -> Unit = { _, _ -> }
    ) = downloadApk(
        lifecycleOwner,
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
        lifecycleOwner: LifecycleOwner,
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
    ) {

        EasyHttp.download(lifecycleOwner)
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
    }
}
