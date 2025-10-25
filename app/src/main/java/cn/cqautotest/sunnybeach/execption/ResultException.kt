package cn.cqautotest.sunnybeach.execption

import com.hjq.http.exception.HttpException


/**
 * author : Android 轮子哥 & A Lonely Cat
 * github : https://github.com/getActivity/EasyHttp
 * time   : 2019/06/25
 * desc   : 返回结果异常
 */
class ResultException : HttpException {

    val data: Any?

    constructor(message: String?, data: Any?) : super(message) {
        this.data = data
    }

    constructor(message: String?, cause: Throwable?, data: Any?) : super(message, cause) {
        this.data = data
    }
}