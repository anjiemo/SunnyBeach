package com.example.blogsystem.network;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011J\u0019\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0016J\u0019\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0016J\u0011\u0010\u0019\u001a\u00020\u001aH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001bJ\b\u0010\u001c\u001a\u0004\u0018\u00010\nJ\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\r0\fJ\u0011\u0010\u001e\u001a\u00020\u001fH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001bJ\u001b\u0010 \u001a\u00020\n2\b\b\u0002\u0010!\u001a\u00020\u0004H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\"J\u0014\u0010#\u001a\u00020\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006$"}, d2 = {"Lcom/example/blogsystem/network/Repository;", "", "()V", "mCurrentPage", "", "mMusicApi", "Lcom/example/blogsystem/network/api/MusicApi;", "mPhotoApi", "Lcom/example/blogsystem/network/api/PhotoApi;", "mPhotoBean", "Lcom/example/blogsystem/network/model/HomePhotoBean;", "mPhotoList", "Ljava/util/ArrayList;", "Lcom/example/blogsystem/network/model/HomePhotoBean$Res$Vertical;", "addLocalPhotoList", "", "photoList", "", "checkLoginQrCode", "Lcom/example/blogsystem/network/model/music/LoginQrCheck;", "key", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createLoginQrCode", "Lcom/example/blogsystem/network/model/music/LoginQrCreate;", "getLoginQrKey", "Lcom/example/blogsystem/network/model/music/LoginQrKey;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPhotoBean", "loadCachePhotoList", "loadHomeBannerList", "Lcom/example/blogsystem/network/model/HomeBannerBean;", "loadPhotoList", "limit", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setLocalPhotoList", "app_debug"})
public final class Repository {
    private static final com.example.blogsystem.network.api.MusicApi mMusicApi = null;
    private static final com.example.blogsystem.network.api.PhotoApi mPhotoApi = null;
    private static com.example.blogsystem.network.model.HomePhotoBean mPhotoBean;
    private static final java.util.ArrayList<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical> mPhotoList = null;
    private static int mCurrentPage = 0;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.network.Repository INSTANCE = null;
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.blogsystem.network.model.HomePhotoBean getPhotoBean() {
        return null;
    }
    
    public final void setLocalPhotoList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical> photoList) {
    }
    
    public final void addLocalPhotoList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical> photoList) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical> loadCachePhotoList() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object loadPhotoList(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.HomePhotoBean> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object loadHomeBannerList(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.HomeBannerBean> p0) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLoginQrKey(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.music.LoginQrKey> p0) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object createLoginQrCode(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.music.LoginQrCreate> p1) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object checkLoginQrCode(@org.jetbrains.annotations.NotNull()
    java.lang.String key, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.blogsystem.network.model.music.LoginQrCheck> p1) {
        return null;
    }
    
    private Repository() {
        super();
    }
}