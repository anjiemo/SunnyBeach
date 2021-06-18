package com.example.blogsystem.viewmodel.app;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0012\u001a\u00020\u00132\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0015J\u00a0\u0002\u0010\u0016\u001a\n \u0018*\u0004\u0018\u00010\u00170\u00172\u0006\u0010\u0019\u001a\u00020\u001a2%\b\u0002\u0010\u001b\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0004\u0012\u00020!0\u001c2:\b\u0002\u0010\"\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0013\u0012\u00110$\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(%\u0012\u0004\u0012\u00020!0#2%\b\u0002\u0010&\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0004\u0012\u00020!0\u001c2B\b\u0002\u0010\'\u001a<\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u001b\u0012\u0019\u0018\u00010(j\u0004\u0018\u0001`)\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(*\u0012\u0004\u0012\u00020!0#2:\b\u0002\u0010+\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0013\u0012\u00110\u001a\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(\u0019\u0012\u0004\u0012\u00020!0#J\u00d2\u0002\u0010\u0016\u001a\n \u0018*\u0004\u0018\u00010\u00170\u00172\b\b\u0002\u0010,\u001a\u00020-2\b\u0010 \u001a\u0004\u0018\u00010\u001d2\b\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\u0010.\u001a\u0004\u0018\u00010\u00152\b\b\u0002\u0010/\u001a\u0002002\u0006\u0010\u0019\u001a\u00020\u001a2%\b\u0002\u0010\u001b\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0004\u0012\u00020!0\u001c2:\b\u0002\u0010\"\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0013\u0012\u00110$\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(%\u0012\u0004\u0012\u00020!0#2%\b\u0002\u0010&\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0004\u0012\u00020!0\u001c2B\b\u0002\u0010\'\u001a<\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u001b\u0012\u0019\u0018\u00010(j\u0004\u0018\u0001`)\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(*\u0012\u0004\u0012\u00020!0#2:\b\u0002\u0010+\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001d\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b( \u0012\u0013\u0012\u00110\u001a\u00a2\u0006\f\b\u001e\u0012\b\b\u001f\u0012\u0004\b\b(\u0019\u0012\u0004\u0012\u00020!0#J\u0006\u00101\u001a\u00020\u0013R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011\u00a8\u00062"}, d2 = {"Lcom/example/blogsystem/viewmodel/app/AppViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_appUpdateState", "Landroidx/lifecycle/MutableLiveData;", "Lcom/example/blogsystem/viewmodel/app/AppUpdateState;", "api", "Lcom/example/blogsystem/network/api/AppApi;", "getApi", "()Lcom/example/blogsystem/network/api/AppApi;", "api$delegate", "Lkotlin/Lazy;", "appUpdateState", "Landroidx/lifecycle/LiveData;", "getAppUpdateState", "()Landroidx/lifecycle/LiveData;", "checkAppVersionUpdate", "Lkotlinx/coroutines/Job;", "url", "", "downloadApk", "Lcom/hjq/http/request/DownloadRequest;", "kotlin.jvm.PlatformType", "appUpdateInfo", "Lcom/example/blogsystem/model/AppUpdateInfo;", "onStart", "Lkotlin/Function1;", "Ljava/io/File;", "Lkotlin/ParameterName;", "name", "file", "", "onProgress", "Lkotlin/Function2;", "", "progress", "onComplete", "onError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "e", "onEnd", "method", "Lcom/hjq/http/model/HttpMethod;", "apkHash", "forceUpdate", "", "initSDK", "app_debug"})
public final class AppViewModel extends androidx.lifecycle.AndroidViewModel {
    private final kotlin.Lazy api$delegate = null;
    private androidx.lifecycle.MutableLiveData<com.example.blogsystem.viewmodel.app.AppUpdateState> _appUpdateState;
    
    private final com.example.blogsystem.network.api.AppApi getApi() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.blogsystem.viewmodel.app.AppUpdateState> getAppUpdateState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job checkAppVersionUpdate(@org.jetbrains.annotations.Nullable()
    java.lang.String url) {
        return null;
    }
    
    public final com.hjq.http.request.DownloadRequest downloadApk(@org.jetbrains.annotations.NotNull()
    com.example.blogsystem.model.AppUpdateInfo appUpdateInfo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.io.File, kotlin.Unit> onStart, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.io.File, ? super java.lang.Integer, kotlin.Unit> onProgress, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.io.File, kotlin.Unit> onComplete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.io.File, ? super java.lang.Exception, kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.io.File, ? super com.example.blogsystem.model.AppUpdateInfo, kotlin.Unit> onEnd) {
        return null;
    }
    
    public final com.hjq.http.request.DownloadRequest downloadApk(@org.jetbrains.annotations.NotNull()
    com.hjq.http.model.HttpMethod method, @org.jetbrains.annotations.Nullable()
    java.io.File file, @org.jetbrains.annotations.Nullable()
    java.lang.String url, @org.jetbrains.annotations.Nullable()
    java.lang.String apkHash, boolean forceUpdate, @org.jetbrains.annotations.NotNull()
    com.example.blogsystem.model.AppUpdateInfo appUpdateInfo, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.io.File, kotlin.Unit> onStart, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.io.File, ? super java.lang.Integer, kotlin.Unit> onProgress, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.io.File, kotlin.Unit> onComplete, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.io.File, ? super java.lang.Exception, kotlin.Unit> onError, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.io.File, ? super com.example.blogsystem.model.AppUpdateInfo, kotlin.Unit> onEnd) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job initSDK() {
        return null;
    }
    
    public AppViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
}