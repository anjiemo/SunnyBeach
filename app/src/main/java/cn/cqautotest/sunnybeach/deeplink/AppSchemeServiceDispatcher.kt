package cn.cqautotest.sunnybeach.deeplink

import android.content.Context
import android.content.Intent
import cn.cqautotest.sunnybeach.aop.Log
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/03/05
 * desc   : DeepLink 调度器
 */
object AppSchemeServiceDispatcher {

    private val mServiceHandlers = mutableMapOf<String, AppSchemeService>()
    private val SCHEME_HANDLES by lazy {
        mapOf(
            "/putFish" to PutFishServiceHandler::class.java,
            "/scanCode" to ScanCodeServiceHandler::class.java
        )
    }

    private fun getOrCreateServiceHandler(path: String): AppSchemeService? {
        return mServiceHandlers[path] ?: SCHEME_HANDLES[path]?.getConstructor()?.newInstance()?.apply {
            mServiceHandlers[path] = this
        }
    }

    @Log
    fun dispatch(context: Context, intent: Intent) {
        val path = intent.getPath()
        Timber.d("dispatch：===> path is $path")
        getOrCreateServiceHandler(path)?.navigation(context, intent)
    }

    private fun Intent.getPath(): String = this.data?.path.orEmpty()
}