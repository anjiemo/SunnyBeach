package cn.cqautotest.sunnybeach.viewmodel.app

import android.app.Activity
import android.app.Application
import androidx.core.app.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.liveData
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.manager.CacheDataManager
import com.blankj.utilcode.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 应用的 ViewModel
 */
class AppViewModel(private val application: Application) : Utils.ActivityLifecycleCallbacks() {

    private val mCallbacks = arrayListOf<LifecycleOwner>()

    private fun getApplication(): Application {
        return application
    }

    override fun onActivityCreated(activity: Activity) {
        super.onActivityCreated(activity)
        if (activity !is ComponentActivity || !mCallbacks.contains(activity)) {
            return
        }
        mCallbacks.add(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is ComponentActivity) {
            mCallbacks.remove(activity)
        }
        super.onActivityDestroyed(activity)
    }

    fun checkAppUpdate() = Repository.checkAppUpdate()

    fun clearCacheMemory() = liveData(Dispatchers.Main) {
        val result = try {
            // 清除内存缓存（必须在主线程）
            GlideApp.get(getApplication()).clearMemory()
            CacheDataManager.clearAllCache(getApplication())
            withContext(Dispatchers.IO) {
                // 清除本地缓存（必须在子线程）
                GlideApp.get(getApplication()).clearDiskCache()
            }
            val totalCacheSize = CacheDataManager.getTotalCacheSize(getApplication())
            Result.success(totalCacheSize)
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }

    companion object {

        private var mINSTANCE: AppViewModel? = null

        fun getAppViewModel(): AppViewModel {
            return mINSTANCE ?: synchronized(this) {
                val instance = AppViewModel(AppApplication.getInstance())
                mINSTANCE = instance
                instance
            }
        }
    }
}
