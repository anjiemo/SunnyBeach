package cn.cqautotest.sunnybeach.viewmodel.app

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.manager.CacheDataManager
import cn.cqautotest.sunnybeach.model.MourningCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 应用的 ViewModel
 */
class AppViewModel private constructor(private val application: Application) {

    private val _mourningCalendarListLiveData = MutableLiveData<List<MourningCalendar>>()
    val mourningCalendarListLiveData: LiveData<List<MourningCalendar>> get() = _mourningCalendarListLiveData

    fun setMourningCalendarList(mourningCalendarList: List<MourningCalendar>) {
        _mourningCalendarListLiveData.value = mourningCalendarList
    }

    fun checkAppUpdate() = Repository.checkAppUpdate()

    fun getMourningCalendar() = Repository.getMourningCalendar()

    fun clearCacheMemory() = liveData(Dispatchers.Main) {
        val result = try {
            // 清除内存缓存（必须在主线程）
            GlideApp.get(application).clearMemory()
            withContext(Dispatchers.IO) {
                CacheDataManager.clearAllCache(application)
                // 清除本地缓存（必须在子线程）
                GlideApp.get(application).clearDiskCache()
            }
            val totalCacheSize = CacheDataManager.getTotalCacheSize(application)
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
