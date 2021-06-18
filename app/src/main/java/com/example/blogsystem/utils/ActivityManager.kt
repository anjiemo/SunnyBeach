package com.example.blogsystem.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.collection.arrayMapOf

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo
 *    gitee  : https://gitee.com/anjiemo
 *    time   : 2021/6/18
 *    desc   : Activity 管理类
 */
object ActivityManager : Application.ActivityLifecycleCallbacks {

    private lateinit var mApplication: Application

    private val mActivityTaskMap = arrayMapOf<String, Activity>()

    // 最后可见 Activity 对象的 TAG
    private var mLastVisibleTag: String? = null

    // 最后不可见 Activity 对象的 TAG
    private var mLastInvisibleTag: String? = null

    /**
     * 初始化，仅第一次有效
     */
    fun init(application: Application) {
        if (::mApplication.isInitialized.not()) {
            mApplication = application
            application.registerActivityLifecycleCallbacks(this)
        }
    }

    /**
     * 获取 Application 对象
     */
    fun getApplication() = mApplication

    /**
     * 最后一个可见的Activity和最后一个不可见的Activity不是同一个（换言之：是同一个 Activity，app则未处于前台），
     * 或栈顶 Activity不为null，则app处于前台
     *
     */
    fun appIsForeground() =
        ((mLastVisibleTag == mLastInvisibleTag).not() || getStackTopActivity() != null)

    /**
     * 获取在栈顶的 Activity
     */
    fun getStackTopActivity() = mActivityTaskMap[mLastVisibleTag]

    /**
     * 销毁所有 Activity
     */
    fun finishAllActivities() {
        finishAllActivities(null)
    }

    /**
     * 销毁除了参数列表中以外的所有 Activity
     */
    fun finishAllActivities(vararg clazzArray: Class<out Activity>?) {
        val keys: Array<String> = mActivityTaskMap.keys.toTypedArray()
        for (key in keys) {
            val activity: Activity? = mActivityTaskMap[key]
            activity ?: continue
            if (activity.isFinishing.not()) {
                var isWhitelistActivity = false
                for (clazz in clazzArray) {
                    if (activity.javaClass == clazz) {
                        isWhitelistActivity = true
                    }
                }
                // 如果不是白名单上面的 Activity 就销毁掉
                if (!isWhitelistActivity) {
                    activity.finish()
                    mActivityTaskMap.remove(key)
                }
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        setLastVisibleTag(activity)
        mActivityTaskMap[mLastVisibleTag] = activity
    }

    override fun onActivityStarted(activity: Activity) {
        setLastVisibleTag(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        setLastVisibleTag(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        setLastInvisibleTag(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        setLastInvisibleTag(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        val objectTag = getObjectTag(activity)
        mActivityTaskMap.remove(objectTag)
        if (objectTag == mLastVisibleTag) {
            // 清除当前标记
            mLastVisibleTag = null
        }
    }

    private fun setLastInvisibleTag(activity: Activity) {
        mLastInvisibleTag = getObjectTag(activity)
    }

    private fun setLastVisibleTag(activity: Activity) {
        mLastVisibleTag = getObjectTag(activity)
    }

    /**
     * 对象所在的包名 + 对象的内存地址
     */
    private fun getObjectTag(@NonNull any: Any): String =
        any::class.java.name + Integer.toHexString(any.hashCode())
}