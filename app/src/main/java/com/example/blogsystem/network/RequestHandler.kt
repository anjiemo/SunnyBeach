package com.example.blogsystem.network

import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.GsonUtils
import com.example.blogsystem.R
import com.example.blogsystem.base.App
import com.example.blogsystem.model.BaseResponse
import com.example.blogsystem.utils.DEFAULT_HTTP_OK_CODE
import com.google.gson.JsonSyntaxException
import com.hjq.http.EasyLog
import com.hjq.http.config.IRequestHandler
import com.hjq.http.exception.DataException
import com.hjq.http.exception.ResponseException
import com.hjq.http.exception.ResultException
import com.hjq.http.exception.TokenException
import okhttp3.Headers
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type

class RequestHandler : IRequestHandler {
    override fun requestSucceed(
        lifecycle: LifecycleOwner?,
        response: Response?,
        type: Type?
    ): Any? {
        if (response == null) {
            return null
        }
        if (Response::class.java == type) {
            return response
        }
        if (!response.isSuccessful) {
            // 返回响应异常
            throw ResponseException(
                App.get()
                    .getString(R.string.http_response_error) + "，responseCode：" + response.code + "，message：" + response.message,
                response
            )
        }
        if (Headers::class.java == type) {
            return response.headers
        }
        val body = response.body ?: return null
        if (InputStream::class.java == type) {
            return body.byteStream()
        }
        val text: String = try {
            body.string()
        } catch (e: IOException) {
            // 返回结果读取异常
            throw DataException(App.get().getString(R.string.http_data_explain_error), e)
        }
        // 打印这个 Json 或者文本
        EasyLog.json(text)
        if (String::class.java == type) {
            return text
        }
        if (JSONObject::class.java == type) {
            return try {
                // 如果这是一个 JSONObject 对象
                JSONObject(text)
            } catch (e: JSONException) {
                throw DataException(App.get().getString(R.string.http_data_explain_error), e)
            }
        }
        if (JSONArray::class.java == type) {
            return try {
                // 如果这是一个 JSONArray 对象
                JSONArray(text)
            } catch (e: JSONException) {
                throw DataException(App.get().getString(R.string.http_data_explain_error), e)
            }
        }
        val result: Any?
        result = try {
            if (type != null) {
                GsonUtils.fromJson(text, type)
            } else {
                null
            }
        } catch (e: JsonSyntaxException) {
            // 返回结果读取异常
            throw DataException(App.get().getString(R.string.http_data_explain_error), e)
        }
        if (result is BaseResponse<*>) {
            val model: BaseResponse<*> = result as BaseResponse<*>
            if (model.code === DEFAULT_HTTP_OK_CODE) {
                // 代表执行成功
                return result
            } else if (model.code === 1111) {
                // 代表登录失效，需要重新登录
                throw TokenException(App.get().getString(R.string.http_token_error))
            }
            throw ResultException(model.message, model)
        }
        return result
    }

    override fun requestFail(lifecycle: LifecycleOwner?, e: Exception?): Exception {
        return e ?: RuntimeException("请求失败")
    }
}