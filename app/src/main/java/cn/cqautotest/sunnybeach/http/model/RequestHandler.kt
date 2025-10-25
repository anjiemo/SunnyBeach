package cn.cqautotest.sunnybeach.http.model

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.execption.ResultException
import cn.cqautotest.sunnybeach.execption.TokenException
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import com.google.gson.JsonSyntaxException
import com.hjq.gson.factory.GsonFactory
import com.hjq.http.EasyLog
import com.hjq.http.config.IRequestHandler
import com.hjq.http.exception.CancelException
import com.hjq.http.exception.DataException
import com.hjq.http.exception.HttpException
import com.hjq.http.exception.NetworkException
import com.hjq.http.exception.ResponseException
import com.hjq.http.exception.ServerException
import com.hjq.http.exception.TimeoutException
import com.hjq.http.request.HttpRequest
import com.tencent.mmkv.MMKV
import okhttp3.Headers
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/07
 *    desc   : 请求处理类
 */
class RequestHandler constructor(private val application: Application) : IRequestHandler {

    private val mmkv: MMKV = MMKV.mmkvWithID("http_cache_id")

    @Throws(Throwable::class)
    override fun requestSuccess(httpRequest: HttpRequest<*>, response: Response, type: Type): Any {
        if ((Response::class.java == type)) {
            return response
        }
        if (!response.isSuccessful) {
            // 返回响应异常
            throw ResponseException(
                application.getString(R.string.http_response_error) + "，responseCode：" + response.code + "，message：" + response.message,
                response
            )
        }
        if ((Headers::class.java == type)) {
            return response.headers
        }
        val body: ResponseBody = response.body ?: throw ResponseException(
            application.getString(R.string.http_response_error) + "，responseCode：" + response.code + "，message：" + response.message,
            response
        )
        if ((InputStream::class.java == type)) {
            return body.byteStream()
        }

        val text: String
        try {
            text = body.string()
        } catch (e: IOException) {
            // 返回结果读取异常
            throw DataException(application.getString(R.string.http_data_explain_error), e)
        }

        // 打印这个 Json 或者文本
        EasyLog.printJson(httpRequest, text)
        if ((String::class.java == type)) {
            return text
        }

        if ((JSONObject::class.java == type)) {
            try {
                // 如果这是一个 JSONObject 对象
                return JSONObject(text)
            } catch (e: JSONException) {
                throw DataException(application.getString(R.string.http_data_explain_error), e)
            }
        }

        if ((JSONArray::class.java == type)) {
            try {
                // 如果这是一个 JSONArray 对象
                return JSONArray(text)
            } catch (e: JSONException) {
                throw DataException(application.getString(R.string.http_data_explain_error), e)
            }
        }

        val result: Any?
        try {
            result = GsonFactory.getSingletonGson().fromJson(text, type)
        } catch (e: JsonSyntaxException) {
            // 返回结果读取异常
            throw DataException(application.getString(R.string.http_data_explain_error), e)
        }

        if (result is HttpData<*>) {
            val model: HttpData<*> = result
            if (model.isRequestSucceed()) {
                // 代表执行成功
                return result
            }
            if (model.isTokenFailure()) {
                // 代表登录失效，需要重新登录
                throw TokenException(application.getString(R.string.http_token_error))
            }
            throw ResultException(model.getMessage(), model)
        }
        return result ?: DataException(application.getString(R.string.http_data_explain_error))
    }

    override fun requestFail(httpRequest: HttpRequest<*>, e: Throwable): Throwable {
        // 判断这个异常是不是自己抛的
        if (e is HttpException) {
            if (e is TokenException) {
                // 登录信息失效，跳转到登录页
                val application: Application = ActivityManager.getInstance().getApplication()
                val intent = Intent(application, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                application.startActivity(intent)
                // 销毁除了登录页之外的 Activity
                ActivityManager.getInstance().finishAllActivities(LoginActivity::class.java)
            }
            return e
        }
        if (e is SocketTimeoutException) {
            return TimeoutException(application.getString(R.string.http_server_out_time), e)
        }
        if (e is UnknownHostException) {
            val info: NetworkInfo? = (application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            // 判断网络是否连接
            if (info == null || !info.isConnected) {
                // 没有连接就是网络异常
                return NetworkException(application.getString(R.string.http_network_error), e)
            }

            // 有连接就是服务器的问题
            return ServerException(application.getString(R.string.http_server_error), e)
        }
        if (e is IOException) {
            // e = new CancelException(context.getString(R.string.http_request_cancel), e);
            return CancelException("", e)
        }
        return HttpException(e.message, e)
    }

    override fun readCache(httpRequest: HttpRequest<*>, type: Type, cacheTime: Long): Any? {
        val cacheKey: String? = GsonFactory.getSingletonGson().toJson(httpRequest.requestApi)
        val cacheValue: String? = mmkv.getString(cacheKey, null)
        if ((cacheValue == null) || ("" == cacheValue) || ("{}" == cacheValue)) {
            return null
        }
        EasyLog.printLog(httpRequest, "---------- cacheKey ----------")
        EasyLog.printJson(httpRequest, cacheKey)
        EasyLog.printLog(httpRequest, "---------- cacheValue ----------")
        EasyLog.printJson(httpRequest, cacheValue)
        return GsonFactory.getSingletonGson().fromJson(cacheValue, type)
    }

    override fun writeCache(httpRequest: HttpRequest<*>, response: Response, result: Any): Boolean {
        val cacheKey: String? = GsonFactory.getSingletonGson().toJson(httpRequest.requestApi)
        val cacheValue: String? = GsonFactory.getSingletonGson().toJson(result)
        if ((cacheValue == null) || ("" == cacheValue) || ("{}" == cacheValue)) {
            return false
        }
        EasyLog.printLog(httpRequest, "---------- cacheKey ----------")
        EasyLog.printJson(httpRequest, cacheKey)
        EasyLog.printLog(httpRequest, "---------- cacheValue ----------")
        EasyLog.printJson(httpRequest, cacheValue)
        return mmkv.putString(cacheKey, cacheValue).commit()
    }
}