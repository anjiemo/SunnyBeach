package cn.cqautotest.sunnybeach.util

import android.app.Application

object PushHelper {

    @JvmStatic
    fun init(application: Application) {
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
}