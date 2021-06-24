package cn.cqautotest.sunnybeach.utils

import cn.cqautotest.sunnybeach.app.AppApplication
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent

class PushHelper {

    companion object {
        @JvmStatic
        fun init() {
            //获取消息推送实例
            val pushAgent = PushAgent.getInstance(AppApplication.getInstance())
            //注册推送服务，每次调用register方法都会回调该接口
            pushAgent.register(object : IUmengRegisterCallback {
                override fun onSuccess(deviceToken: String) {
                    //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                    logByDebug(msg = "注册成功：deviceToken：===> $deviceToken")
                }

                override fun onFailure(errCode: String, errDesc: String) {
                    logByDebug(msg = "注册失败：===> code:$errCode, desc:$errDesc")
                }
            })
        }
    }
}