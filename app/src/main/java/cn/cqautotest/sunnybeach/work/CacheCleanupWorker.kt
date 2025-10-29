package cn.cqautotest.sunnybeach.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.manager.AppManager
import cn.cqautotest.sunnybeach.manager.CacheDataManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/06
 * desc   : 缓存清理 Worker
 */
class CacheCleanupWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val isAutoCleanCache = AppManager.isAutoCleanCache()
        Timber.d("doWork:===> isAutoCleanCache $isAutoCleanCache")
        if (isAutoCleanCache.not()) return Result.success()
        Timber.d("doWork:===> start clean cache...")
        try {
            // 清除内存缓存（必须在主线程）
            withContext(Dispatchers.Main) { GlideApp[applicationContext].clearMemory() }
            withContext(Dispatchers.IO) {
                CacheDataManager.clearAllCache(applicationContext)
                // 清除本地缓存（必须在子线程）
                GlideApp[applicationContext].clearDiskCache()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        Timber.d("doWork:===> end clean cache...")
        return Result.success()
    }
}