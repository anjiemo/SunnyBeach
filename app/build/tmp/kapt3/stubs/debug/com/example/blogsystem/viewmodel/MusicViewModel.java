package com.example.blogsystem.viewmodel;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0011\u0010\u0014\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0016J\u0019\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0007H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0019J\u0011\u0010\u001a\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\r8F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\r8F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u000fR\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\t0\r8F\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u000f\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001b"}, d2 = {"Lcom/example/blogsystem/viewmodel/MusicViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "_qrBitmapLiveData", "Landroidx/lifecycle/MutableLiveData;", "Landroid/graphics/Bitmap;", "_qrKeyLiveData", "Lcom/example/blogsystem/network/model/music/LoginQrKey;", "_qrStateLiveData", "Lcom/example/blogsystem/network/model/music/LoginQrCheck;", "mQrImg", "", "qrBitmapLiveData", "Landroidx/lifecycle/LiveData;", "getQrBitmapLiveData", "()Landroidx/lifecycle/LiveData;", "qrKeyLiveData", "getQrKeyLiveData", "qrStateLiveData", "getQrStateLiveData", "checkQrCodeState", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadQrBitmap", "loginQrKey", "(Lcom/example/blogsystem/network/model/music/LoginQrKey;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "refreshQrCodeKey", "app_debug"})
public final class MusicViewModel extends androidx.lifecycle.ViewModel {
    private java.lang.String mQrImg = "";
    private final androidx.lifecycle.MutableLiveData<com.example.blogsystem.network.model.music.LoginQrKey> _qrKeyLiveData = null;
    private final androidx.lifecycle.MutableLiveData<android.graphics.Bitmap> _qrBitmapLiveData = null;
    private final androidx.lifecycle.MutableLiveData<com.example.blogsystem.network.model.music.LoginQrCheck> _qrStateLiveData = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.blogsystem.network.model.music.LoginQrKey> getQrKeyLiveData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<android.graphics.Bitmap> getQrBitmapLiveData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.blogsystem.network.model.music.LoginQrCheck> getQrStateLiveData() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object refreshQrCodeKey(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p0) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object loadQrBitmap(@org.jetbrains.annotations.NotNull()
    com.example.blogsystem.network.model.music.LoginQrKey loginQrKey, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object checkQrCodeState(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p0) {
        return null;
    }
    
    public MusicViewModel() {
        super();
    }
}