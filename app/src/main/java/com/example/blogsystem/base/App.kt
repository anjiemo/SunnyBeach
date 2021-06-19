package com.example.blogsystem.base

import android.app.Application
import com.bumptech.glide.Glide
import com.example.blogsystem.db.CookieRoomDatabase
import com.example.blogsystem.viewmodel.SingletonManager

class App : Application() {

    // 懒加载使用，因此数据库和存储库仅在需要时创建，而不是在应用程序启动时创建
    val database by lazy { CookieRoomDatabase.getDatabase(this) }
    private val appViewModel by lazy { SingletonManager.appViewModel }

    override fun onCreate() {
        super.onCreate()
        mApp = this
        initApp()
    }

    private fun initApp() {
        appViewModel.initSDK()
//        // 小米更新SDK
//        XiaomiUpdateAgent.update(this, AppConfig.isDebug())
//        XiaomiUpdateAgent.setUpdateListener { i, updateResponse ->
//            logByDebug(msg = "===> app update ${updateResponse.toJson()}")
//            simpleToast(updateResponse.toJson())
//        }
//        XiaomiUpdateAgent.setUpdateMethod(Constants.UpdateMethod.DOWNLOAD_MANAGER)
//        XiaomiUpdateAgent.arrange()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(this).onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Glide.get(this).onTrimMemory(level)
    }

    companion object {
        private lateinit var mApp: App
        fun get(): App = mApp
    }
}