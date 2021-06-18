package com.example.blogsystem.network.api;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001b\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J%\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0003\u0010\t\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0011\u0010\u000b\u001a\u00020\fH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u000e"}, d2 = {"Lcom/example/blogsystem/network/api/MusicApi;", "", "checkLoginQrCode", "Lcom/example/blogsystem/network/model/music/LoginQrCheck;", "key", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createLoginQrCode", "Lcom/example/blogsystem/network/model/music/LoginQrCreate;", "qrImg", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLoginQrCodeKey", "Lcom/example/blogsystem/network/model/music/LoginQrKey;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface MusicApi {
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.GET(value = "login/qr/key")
    public abstract java.lang.Object getLoginQrCodeKey(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.music.LoginQrKey> p0);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.GET(value = "login/qr/create")
    public abstract java.lang.Object createLoginQrCode(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Query(value = "key")
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    @retrofit2.http.Query(value = "qrimg")
    java.lang.String qrImg, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.music.LoginQrCreate> p2);
    
    @org.jetbrains.annotations.Nullable()
    @retrofit2.http.GET(value = "login/qr/check")
    public abstract java.lang.Object checkLoginQrCode(@org.jetbrains.annotations.NotNull()
    @retrofit2.http.Query(value = "key")
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.music.LoginQrCheck> p1);
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3)
    public final class DefaultImpls {
    }
}