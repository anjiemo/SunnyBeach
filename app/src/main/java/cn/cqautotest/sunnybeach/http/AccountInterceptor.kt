package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.db.SobCacheManager
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.model.ApiResponse
import cn.cqautotest.sunnybeach.ui.activity.HomeActivity
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import cn.cqautotest.sunnybeach.util.fromJson
import cn.cqautotest.sunnybeach.util.unicodeToString
import com.blankj.utilcode.util.ThreadUtils
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/26
 * desc   : 账号拦截器，如果检测到账号未登录，则弹出提示并跳转至登录界面
 */
class AccountInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()

        // 按需要添加请求头
        SobCacheManager.addHeadersByNeed(request, requestBuilder)

        val response = chain.proceed(requestBuilder.build())
        val headers = response.headers
        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()

        // 根据需要保存请求头
        SobCacheManager.saveHeadersByNeed(request, headers)

        if (response.promisesBody().not() || bodyHasUnknownEncoding(headers)) {
            return response
        }
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer
        if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
            GzipSource(buffer.clone()).use { gzippedResponseBody ->
                buffer = Buffer()
                buffer.writeAll(gzippedResponseBody)
            }
        }
        val contentType = responseBody.contentType()
        val charset: Charset =
            contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
        if (buffer.isProbablyUtf8().not()) {
            return response
        }
        if (contentLength != 0L) {
            val result = buffer.clone().readString(charset).unicodeToString()
            try {
                val apiResponse: ApiResponse<Any> = fromJson(result)
                when (apiResponse.getCode()) {
                    // 账号未登录时的状态码
                    11126 -> interceptUserAction()
                    // 可以在此处拓展拦截更多的 case
                }
            } catch (e: Exception) {
                // Do nothing.
            }
        }
        return response
    }

    private fun interceptUserAction() {
        ThreadUtils.getMainHandler().post {
            val am = ActivityManager.getInstance()
            val topActivity = am.topActivity
            if (topActivity is HomeActivity || topActivity is LoginActivity) {
                return@post
            }
            // simpleToast(R.string.http_token_error)
            // LoginActivity.start(topActivity, "", "")
        }
    }

    private fun Buffer.isProbablyUtf8(): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = size.coerceAtMost(64)
            copyTo(prefix, 0, byteCount)
            for (i in 0 until 16) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (_: EOFException) {
            return false // Truncated UTF-8 sequence.
        }
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}