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
            override fun onActivityCreated(a: Activity, savedInstanceState: Bundle?) {
                if (a !== activity) return
                getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }

            override fun onActivityStarted(a: Activity) {
                if (a !== activity) return
                getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_START)
            }

            override fun onActivityResumed(a: Activity) {
                if (a !== activity) return
                getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            }

            override fun onActivityPaused(a: Activity) {
                if (a !== activity) return
                getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            }

            override fun onActivityStopped(a: Activity) {
                if (a !== activity) return
                getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            }

            override fun onActivitySaveInstanceState(a: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(a: Activity) {
                if (a !== activity) return
                getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                activity.application.unregisterActivityLifecycleCallbacks(this)
            }
        })
    }
}
