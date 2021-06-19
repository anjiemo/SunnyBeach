package com.example.blogsystem.viewmodel.app;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005J\u0012\u0010\u0013\u001a\u00020\u00142\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0016J\u00a0\u0002\u0010\u0017\u001a\n \u0019*\u0004\u0018\u00010\u00180\u00182\u0006\u0010\u001a\u001a\u00020\u001b2%\b\u0002\u0010\u001c\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\"0\u001d2:\b\u0002\u0010#\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0013\u0012\u00110%\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(&\u0012\u0004\u0012\u00020\"0$2%\b\u0002\u0010\'\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\"0\u001d2B\b\u0002\u0010(\u001a<\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u001b\u0012\u0019\u0018\u00010)j\u0004\u0018\u0001`*\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(+\u0012\u0004\u0012\u00020\"0$2:\b\u0002\u0010,\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0013\u0012\u00110\u001b\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(\u001a\u0012\u0004\u0012\u00020\"0$J\u00d2\u0002\u0010\u0017\u001a\n \u0019*\u0004\u0018\u00010\u00180\u00182\b\b\u0002\u0010-\u001a\u00020.2\b\u0010!\u001a\u0004\u0018\u00010\u001e2\b\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010/\u001a\u0004\u0018\u00010\u00162\b\b\u0002\u00100\u001a\u0002012\u0006\u0010\u001a\u001a\u00020\u001b2%\b\u0002\u0010\u001c\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\"0\u001d2:\b\u0002\u0010#\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0013\u0012\u00110%\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(&\u0012\u0004\u0012\u00020\"0$2%\b\u0002\u0010\'\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0004\u0012\u00020\"0\u001d2B\b\u0002\u0010(\u001a<\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u001b\u0012\u0019\u0018\u00010)j\u0004\u0018\u0001`*\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(+\u0012\u0004\u0012\u00020\"0$2:\b\u0002\u0010,\u001a4\u0012\u0015\u0012\u0013\u0018\u00010\u001e\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(!\u0012\u0013\u0012\u00110\u001b\u00a2\u0006\f\b\u001f\u0012\b\b \u0012\u0004\b\b(\u001a\u0012\u0004\u0012\u00020\"0$J\u0006\u00102\u001a\u00020\u0014J\u0012\u00103\u001a\u00020\"2\b\u00104\u001a\u0004\u0018\u000105H\u0016J\b\u00106\u001a\u00020\"H\u0016R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\b0\u00108F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012\u00a8\u00067"}, d2 = {"Lcom/example/blogsystem/viewmodel/app/AppViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "Lcom/blankj/utilcode/util/NetworkUtils$OnNetworkStatusChangedListener;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_appUpdateState", "Landroidx/lifecycle/MutableLiveData;", "Lcom/example/blogsystem/viewmodel/app/AppUpdateState;", "api", "Lcom/example/blogsystem/network/api/AppApi;", "getApi", "()Lcom/example/blogsystem/network/api/AppApi;", "api$delegate", "Lkotlin/Lazy;", "appUpdateState", "Landroidx/lifecycle/LiveData;", "getAppUpdateState", "()Landroidx/lifecycle/LiveData;", "checkAppVersionUpdate", "Lkotlinx/coroutines/Job;", "url", "", "downloadApk", "Lcom/hjq/http/request/DownloadRequest;", "kotlin.jvm.PlatformType", "appUpdateInfo", "Lcom/example/blogsystem/model/AppUpdateInfo;", "onStart", "Lkotlin/Function1;", "Ljava/io/File;", "Lkotlin/ParameterName;", "name", "file", "", "onProgress", "Lkotlin/Function2;", "", "progress", "onComplete", "onError", "Ljava/lang/Exception;", "Lkotlin/Exception;", "e", "onEnd", "method", "Lcom/hjq/http/model/HttpMethod;", "apkHash", "forceUpdate", "", "initSDK", "onConnected", "networkType", "Lcom/blankj/utilcode/util/NetworkUtils$NetworkType;", "onDisconnected", "app_debug"})
public final class AppViewModel extends androidx.lifecycle.AndroidViewModel implements com.blankj.utilcode.util.NetworkUtils.OnNetworkStatusChangedListener {
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
    
    @java.lang.Override()
    public void onDisconnected() {
    }
    
    @java.lang.Override()
    public void onConnected(@org.jetbrains.annotations.Nullable()
    com.blankj.utilcode.util.NetworkUtils.NetworkType networkType) {
    }
    
    public AppViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
}