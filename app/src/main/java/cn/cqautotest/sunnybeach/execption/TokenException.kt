package cn.cqautotest.sunnybeach.execption

import com.hjq.http.exception.HttpException

/**
 * author : Android 轮子哥 & A Lonely Cat
 * github : https://github.com/getActivity/EasyHttp
 * time   : 2019/05/19
 * desc   : Token 失效异常
 */
class TokenException : HttpException {

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}