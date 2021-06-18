package com.example.blogsystem.view;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u0000 \u000f2\u00020\u00012\u00020\u0002:\u0001\u000fB\u0011\b\u0016\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u0005B\u001b\b\u0017\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\bJ\n\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\b\u0010\u000b\u001a\u00020\fH\u0007J\b\u0010\r\u001a\u00020\fH\u0017J\b\u0010\u000e\u001a\u00020\fH\u0017\u00a8\u0006\u0010"}, d2 = {"Lcom/example/blogsystem/view/XWebView;", "Lcom/tencent/smtt/sdk/WebView;", "Landroidx/lifecycle/LifecycleObserver;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "getUrl", "", "onDestroy", "", "onPause", "onResume", "Companion", "app_debug"})
public final class XWebView extends com.tencent.smtt.sdk.WebView implements androidx.lifecycle.LifecycleObserver {
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.view.XWebView.Companion Companion = null;
    
    /**
     * 获取当前的url
     *
     * @return 返回原始的url,因为有些url是被WebView解码过的
     */
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.String getUrl() {
        return null;
    }
    
    @java.lang.Override()
    @androidx.lifecycle.OnLifecycleEvent(value = androidx.lifecycle.Lifecycle.Event.ON_RESUME)
    public void onResume() {
    }
    
    @java.lang.Override()
    @androidx.lifecycle.OnLifecycleEvent(value = androidx.lifecycle.Lifecycle.Event.ON_PAUSE)
    public void onPause() {
    }
    
    @androidx.lifecycle.OnLifecycleEvent(value = androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
    public final void onDestroy() {
    }
    
    public XWebView(@org.jetbrains.annotations.Nullable()
    android.content.Context context) {
        super(null, false);
    }
    
    @android.annotation.SuppressLint(value = {"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    public XWebView(@org.jetbrains.annotations.Nullable()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null, false);
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0007\u00a8\u0006\u0006"}, d2 = {"Lcom/example/blogsystem/view/XWebView$Companion;", "", "()V", "getFixedContext", "Landroid/content/Context;", "context", "app_debug"})
    public static final class Companion {
        
        /**
         * 修复原生 WebView 和 AndroidX 在 Android 5.x 上面崩溃的问题
         *
         * 博客地址：https://blog.csdn.net/qq_34206863/article/details/103660307
         */
        @org.jetbrains.annotations.Nullable()
        @android.annotation.SuppressLint(value = {"ObsoleteSdkInt"})
        public final android.content.Context getFixedContext(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}