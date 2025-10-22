package cn.cqautotest.sunnybeach.util
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.LogUtils
import org.json.JSONException
import org.json.JSONObject

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/05/16
 * desc   : Activity 参数打印工具类
 */
class ActivityLifecycleLogger : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogUtils.d("onActivityCreated：===> exe...", activity, convertToJson(activity.intent))
        if (activity is AppCompatActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object :FragmentManager.FragmentLifecycleCallbacks() {

                override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                    LogUtils.d("onFragmentCreated：===> exe...", f, savedInstanceState?.let { convertToJson(it) })
                }

                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    LogUtils.d("onFragmentResumed：===> exe...", f)
                }
            }, true)
        }
    }

    private fun convertToJson(intent: Intent): String {
        return intent.extras?.let { convertToJson(it) } ?: JSONObject().toString()
    }

    private fun convertToJson(bundle: Bundle): String {
        val jsonObject = JSONObject()
        for (key in bundle.keySet()) {
            try {
                jsonObject.put(key, bundle[key])
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return jsonObject.toString()
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    companion object {

        @JvmStatic
        val instance: Application.ActivityLifecycleCallbacks by lazy { ActivityLifecycleLogger() }
    }
}