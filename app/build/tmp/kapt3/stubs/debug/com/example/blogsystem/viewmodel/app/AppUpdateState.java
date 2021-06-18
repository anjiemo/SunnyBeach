package com.example.blogsystem.viewmodel.app;

import java.lang.System;

/**
 * App检查更新的状态
 */
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\rJ\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J<\u0010\u0015\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u0016J\u0013\u0010\u0017\u001a\u00020\b2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u000fR\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\u000e\u001a\u0004\b\u0010\u0010\r\u00a8\u0006\u001c"}, d2 = {"Lcom/example/blogsystem/viewmodel/app/AppUpdateState;", "", "checkUpdateError", "", "networkError", "appUpdateInfo", "Lcom/example/blogsystem/model/AppUpdateInfo;", "isDataValid", "", "(Ljava/lang/Integer;Ljava/lang/Integer;Lcom/example/blogsystem/model/AppUpdateInfo;Z)V", "getAppUpdateInfo", "()Lcom/example/blogsystem/model/AppUpdateInfo;", "getCheckUpdateError", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "()Z", "getNetworkError", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/Integer;Ljava/lang/Integer;Lcom/example/blogsystem/model/AppUpdateInfo;Z)Lcom/example/blogsystem/viewmodel/app/AppUpdateState;", "equals", "other", "hashCode", "toString", "", "app_debug"})
public final class AppUpdateState {
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer checkUpdateError = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer networkError = null;
    @org.jetbrains.annotations.Nullable()
    private final com.example.blogsystem.model.AppUpdateInfo appUpdateInfo = null;
    private final boolean isDataValid = false;
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getCheckUpdateError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getNetworkError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.blogsystem.model.AppUpdateInfo getAppUpdateInfo() {
        return null;
    }
    
    public final boolean isDataValid() {
        return false;
    }
    
    public AppUpdateState(@org.jetbrains.annotations.Nullable()
    java.lang.Integer checkUpdateError, @org.jetbrains.annotations.Nullable()
    java.lang.Integer networkError, @org.jetbrains.annotations.Nullable()
    com.example.blogsystem.model.AppUpdateInfo appUpdateInfo, boolean isDataValid) {
        super();
    }
    
    public AppUpdateState() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.blogsystem.model.AppUpdateInfo component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    /**
     * App检查更新的状态
     */
    @org.jetbrains.annotations.NotNull()
    public final com.example.blogsystem.viewmodel.app.AppUpdateState copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer checkUpdateError, @org.jetbrains.annotations.Nullable()
    java.lang.Integer networkError, @org.jetbrains.annotations.Nullable()
    com.example.blogsystem.model.AppUpdateInfo appUpdateInfo, boolean isDataValid) {
        return null;
    }
    
    /**
     * App检查更新的状态
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * App检查更新的状态
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * App检查更新的状态
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}