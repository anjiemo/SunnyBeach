package cn.cqautotest.sunnybeach.http.model

import cn.cqautotest.sunnybeach.other.AppConfig
import com.hjq.http.config.IRequestBodyStrategy
import com.hjq.http.config.IRequestServer
import com.hjq.http.model.RequestBodyType

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2020/10/02
 *    desc   : 服务器配置
 */
class RequestServer : IRequestServer {

    override fun getHost(): String {
        return AppConfig.getHostUrl()
    }

    override fun getBodyType(): IRequestBodyStrategy {
        return RequestBodyType.FORM
    }
}