package cn.cqautotest.sunnybeach.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import android.os.Process
import cn.cqautotest.sunnybeach.ktx.TAG
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import com.xiaomi.mipush.sdk.MiPushClient
import timber.log.Timber

object PushHelper {

    private const val APP_ID = "2882303761519961431"
    private const val APP_KEY = "5911996136431"

    @JvmStatic
    fun init(application: Application) {
        application.initMiPush()
        //获取消息推送实例
        // val pushAgent = PushAgent.getInstance(application)
        // //注册推送服务，每次调用register方法都会回调该接口
        // pushAgent.register(object : IUmengRegisterCallback {
        //     override fun onSuccess(deviceToken: String) {
        //         //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
        //         Timber.d("注册成功：deviceToken：===> $deviceToken")
        //     }
        //
        //     override fun onFailure(errCode: String, errDesc: String) {
        //         Timber.d("注册失败：===> code:$errCode, desc:$errDesc")
        //     }
        // })
    }

    /**
     * 注意：
     * 因为推送服务XMPushService在AndroidManifest.xml中设置为运行在另外一个进程，这导致本Application会被实例化两次，所以我们需要让应用的主进程初始化。
     * 在非MIUI平台下，如果targetSdkVersion>=23，需要动态申请电话和存储权限，请在申请权限后再调用注册接口，否则会注册失败。
     */
    private fun Application.initMiPush() {
        // 初始化 push 推送任务
        takeIf { shouldInit() }?.let { MiPushClient.registerPush(this, APP_ID, APP_KEY) }
        // 打开Log
        val newLogger: LoggerInterface = object : LoggerInterface {
            override fun setTag(tag: String) {
                // ignore
            }

            override fun log(content: String, t: Throwable) {
                Timber.tag(TAG).d(t, content)
            }

            override fun log(content: String) {
                Timber.tag(TAG).d(content)
            }
        }
        Logger.setLogger(this, newLogger)
    }

    private fun Application.shouldInit(): Boolean {
        val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfoList: List<RunningAppProcessInfo> = am.runningAppProcesses.orEmpty()
        val mainProcessName: String = applicationInfo.processName
        val myPid: Int = Process.myPid()
        for (info in processInfoList) {
            if (info.pid == myPid && mainProcessName == info.processName) {
                return true
            }
        }
        return false
    }
}