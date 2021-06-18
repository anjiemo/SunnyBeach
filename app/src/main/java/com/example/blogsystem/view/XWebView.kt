package com.example.blogsystem.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.tencent.smtt.sdk.WebView

class XWebView : WebView, LifecycleObserver {

    constructor(context: Context?) : this(context, null)

    @SuppressLint("SetJavaScriptEnabled", "ObsoleteSdkInt")
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs,
        android.R.attr.webViewStyle
    ) {
        settings.apply {
            // 允许文件访问
            allowFileAccess = true
            // 允许网页定位
            setGeolocationEnabled(true)
            //setSavePassword(true);
            // 开启 JavaScript
            //setSavePassword(true);
            javaScriptEnabled = true
            // 允许网页弹对话框
            javaScriptCanOpenWindowsAutomatically = true
            // 加快网页加载完成的速度，等页面完成再加载图片
            loadsImagesAutomatically = true
            // 本地 DOM 存储（解决加载某些网页出现白板现象）
            domStorageEnabled = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 解决 Android 5.0 上 WebView 默认不允许加载 Http 与 Https 混合内容
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            // 不显示滚动条
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
        }
    }


    /**
     * 获取当前的url
     *
     * @return 返回原始的url,因为有些url是被WebView解码过的
     */
    override fun getUrl(): String? {
        val originalUrl = super.getOriginalUrl()
        // 避免开始时同时加载两个地址而导致的崩溃
        return originalUrl ?: super.getUrl()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        super.onResume()
        resumeTimers()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
        super.onPause()
        pauseTimers()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        (parent as ViewGroup).removeView(this)
        //清除历史记录
        clearHistory()
        //停止加载
        stopLoading()
        //加载一个空白页
        loadUrl("about:blank")
        webChromeClient = null
        webViewClient = null
        //移除WebView所有的View对象
        removeAllViews()
        //销毁此的WebView的内部状态
        destroy()
    }

    companion object {

        /**
         * 修复原生 WebView 和 AndroidX 在 Android 5.x 上面崩溃的问题
         *
         * 博客地址：https://blog.csdn.net/qq_34206863/article/details/103660307
         */
        @SuppressLint("ObsoleteSdkInt")
        fun getFixedContext(context: Context): Context? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                // 不用上下文
                context.createConfigurationContext(Configuration())
            } else context
    }
}