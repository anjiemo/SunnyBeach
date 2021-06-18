package com.example.blogsystem.network;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J&\u0010\u0003\u001a\u00060\u0004j\u0002`\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u000e\u0010\b\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u0005H\u0016J(\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016\u00a8\u0006\u000f"}, d2 = {"Lcom/example/blogsystem/network/RequestHandler;", "Lcom/hjq/http/config/IRequestHandler;", "()V", "requestFail", "Ljava/lang/Exception;", "Lkotlin/Exception;", "lifecycle", "Landroidx/lifecycle/LifecycleOwner;", "e", "requestSucceed", "", "response", "Lokhttp3/Response;", "type", "Ljava/lang/reflect/Type;", "app_debug"})
public final class RequestHandler implements com.hjq.http.config.IRequestHandler {
    
    @org.jetbrains.annotations.Nullable()
    @java.lang.Override()
    public java.lang.Object requestSucceed(@org.jetbrains.annotations.Nullable()
    androidx.lifecycle.LifecycleOwner lifecycle, @org.jetbrains.annotations.Nullable()
    okhttp3.Response response, @org.jetbrains.annotations.Nullable()
    java.lang.reflect.Type type) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.Exception requestFail(@org.jetbrains.annotations.Nullable()
    androidx.lifecycle.LifecycleOwner lifecycle, @org.jetbrains.annotations.Nullable()
    java.lang.Exception e) {
        return null;
    }
    
    public RequestHandler() {
        super();
    }
}