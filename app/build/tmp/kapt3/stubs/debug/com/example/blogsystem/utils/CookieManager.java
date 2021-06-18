package com.example.blogsystem.utils;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\rH\u0016J\u001e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u0016R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Lcom/example/blogsystem/utils/CookieManager;", "Lokhttp3/CookieJar;", "()V", "cookiesViewModel", "Lcom/example/blogsystem/viewmodel/CookiesViewModel;", "getCookiesViewModel", "()Lcom/example/blogsystem/viewmodel/CookiesViewModel;", "cookiesViewModel$delegate", "Lkotlin/Lazy;", "loadForRequest", "", "Lokhttp3/Cookie;", "url", "Lokhttp3/HttpUrl;", "saveFromResponse", "", "cookies", "app_debug"})
public final class CookieManager implements okhttp3.CookieJar {
    private final kotlin.Lazy cookiesViewModel$delegate = null;
    
    private final com.example.blogsystem.viewmodel.CookiesViewModel getCookiesViewModel() {
        return null;
    }
    
    /**
     * 通过主机名获取保存的 cookie
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.util.List<okhttp3.Cookie> loadForRequest(@org.jetbrains.annotations.NotNull()
    okhttp3.HttpUrl url) {
        return null;
    }
    
    /**
     * 保存该主机名下的 cookie
     */
    @java.lang.Override()
    public void saveFromResponse(@org.jetbrains.annotations.NotNull()
    okhttp3.HttpUrl url, @org.jetbrains.annotations.NotNull()
    java.util.List<okhttp3.Cookie> cookies) {
    }
    
    public CookieManager() {
        super();
    }
}