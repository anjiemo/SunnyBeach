package com.example.blogsystem.network;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0018\u001a\u0002H\u0019\"\u0006\b\u0000\u0010\u0019\u0018\u0001H\u0086\b\u00a2\u0006\u0002\u0010\u001aR\u001b\u0010\u0003\u001a\u00020\u00048FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000e\u001a\u00020\u000f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0012\u0010\b\u001a\u0004\b\u0010\u0010\u0011R\u001b\u0010\u0013\u001a\u00020\u00148FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\b\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\u001b"}, d2 = {"Lcom/example/blogsystem/network/ServiceCreator;", "", "()V", "client", "Lokhttp3/OkHttpClient;", "getClient", "()Lokhttp3/OkHttpClient;", "client$delegate", "Lkotlin/Lazy;", "cookieManager", "Lcom/example/blogsystem/utils/CookieManager;", "getCookieManager", "()Lcom/example/blogsystem/utils/CookieManager;", "cookieManager$delegate", "interceptor", "Lokhttp3/logging/HttpLoggingInterceptor;", "getInterceptor", "()Lokhttp3/logging/HttpLoggingInterceptor;", "interceptor$delegate", "retrofit", "Lretrofit2/Retrofit;", "getRetrofit", "()Lretrofit2/Retrofit;", "retrofit$delegate", "create", "T", "()Ljava/lang/Object;", "app_debug"})
public final class ServiceCreator {
    private static final kotlin.Lazy interceptor$delegate = null;
    private static final kotlin.Lazy cookieManager$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy client$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy retrofit$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.network.ServiceCreator INSTANCE = null;
    
    private final okhttp3.logging.HttpLoggingInterceptor getInterceptor() {
        return null;
    }
    
    private final com.example.blogsystem.utils.CookieManager getCookieManager() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final okhttp3.OkHttpClient getClient() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final retrofit2.Retrofit getRetrofit() {
        return null;
    }
    
    private ServiceCreator() {
        super();
    }
}