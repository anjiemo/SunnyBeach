package cn.cqautotest.sunnybeach.action

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2024/01/26
 * desc   : 生命周期行为接口，用于非 ComponentActivity 的子类（如第三方 SDK 的 Activity）实现 LifecycleOwner
 */
interface LifecycleAction : LifecycleOwner {

    fun getLifecycleRegistry(): LifecycleRegistry

    override val lifecycle: Lifecycle
        get() = getLifecycleRegistry()

    fun initLifecycle(activity: Activity) {
        activity.application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            private fun handle(targetActivity: Activity, event: Lifecycle.Event) {
                if (activity === targetActivity) {
                    getLifecycleRegistry().handleLifecycleEvent(event)
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        activity.application.unregisterActivityLifecycleCallbacks(this)
                    }
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = handle(activity, Lifecycle.Event.ON_CREATE)
            override fun onActivityStarted(activity: Activity) = handle(activity, Lifecycle.Event.ON_START)
            override fun onActivityResumed(activity: Activity) = handle(activity, Lifecycle.Event.ON_RESUME)
            override fun onActivityPaused(activity: Activity) = handle(activity, Lifecycle.Event.ON_PAUSE)
            override fun onActivityStopped(activity: Activity) = handle(activity, Lifecycle.Event.ON_STOP)
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) = handle(activity, Lifecycle.Event.ON_DESTROY)
        })
    }
}

/**
 * 更加简洁的 [LifecycleRegistry] 初始化方式。
 * 我们通过显式的 [Application.ActivityLifecycleCallbacks] 来实现生命周期的注入。
 */
fun Activity.lifecycleRegistry() = lazy(LazyThreadSafetyMode.NONE) {
    require(this is LifecycleOwner) { "Activity 必须实现 LifecycleOwner 接口" }
    LifecycleRegistry(this).also { registry ->
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            private fun handle(targetActivity: Activity, event: Lifecycle.Event) {
                if (targetActivity === this@lifecycleRegistry) {
                    registry.handleLifecycleEvent(event)
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        targetActivity.application.unregisterActivityLifecycleCallbacks(this)
                    }
                }
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = handle(activity, Lifecycle.Event.ON_CREATE)
            override fun onActivityStarted(activity: Activity) = handle(activity, Lifecycle.Event.ON_START)
            override fun onActivityResumed(activity: Activity) = handle(activity, Lifecycle.Event.ON_RESUME)
            override fun onActivityPaused(activity: Activity) = handle(activity, Lifecycle.Event.ON_PAUSE)
            override fun onActivityStopped(activity: Activity) = handle(activity, Lifecycle.Event.ON_STOP)
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) = handle(activity, Lifecycle.Event.ON_DESTROY)
        })
    }
}
