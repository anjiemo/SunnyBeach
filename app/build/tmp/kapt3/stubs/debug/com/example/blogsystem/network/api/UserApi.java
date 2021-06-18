package com.example.blogsystem.network.api;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J+\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u00032\b\b\u0001\u0010\b\u001a\u00020\u00072\b\b\u0001\u0010\t\u001a\u00020\nH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000b\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\f"}, d2 = {"Lcom/example/blogsystem/network/api/UserApi;", "", "checkToken", "Lcom/example/blogsystem/model/BaseResponse;", "Lcom/example/blogsystem/model/UserBasicInfo;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "login", "", "captcha", "user", "Lcom/example/blogsystem/model/User;", "(Ljava/lang/String;Lcom/example/blogsystem/model/User;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface UserApi {
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.POST(value = "https://api.sunofbeach.net/uc/user/login/{captcha}")
    public abstract java.lang.Object login(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Path(value = "captcha")
    java.lang.String captcha, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Body()
    com.example.blogsystem.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.model.BaseResponse<java.lang.String>> p2);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.GET(value = "https://api.sunofbeach.net/uc/user/checkToken")
    public abstract java.lang.Object checkToken(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.model.BaseResponse<com.example.blogsystem.model.UserBasicInfo>> p0);
}