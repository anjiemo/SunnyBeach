package com.aliyun.svideo.common.okhttp.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp网络请求日志拦截器，通过日志记录OkHttp所有请求以及响应的细节。
 */
public class LoggingIntcepetor implements Interceptor {
    private final String TAG = getClass().getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Log.d(TAG, "Sending request: " + request.url() + "\n" + request.headers());
        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Log.d(TAG, "Received response for " + response.request().url() + " in "
                + (t2 - t1) / 1e6 + "ms\n" + response.headers());
        return response;
    }
}
