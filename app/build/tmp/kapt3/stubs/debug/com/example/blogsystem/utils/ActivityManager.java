package com.example.blogsystem.utils;

import java.lang.System;

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo
 * gitee  : https://gitee.com/anjiemo
 * time   : 2021/6/18
 * desc   : Activity 管理类
 */
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\n\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\u000eJ3\u0010\r\u001a\u00020\u000e2&\u0010\u000f\u001a\u0014\u0012\u0010\b\u0001\u0012\f\u0012\u0006\b\u0001\u0012\u00020\u0006\u0018\u00010\u00110\u0010\"\f\u0012\u0006\b\u0001\u0012\u00020\u0006\u0018\u00010\u0011\u00a2\u0006\u0002\u0010\u0012J\u0006\u0010\u0013\u001a\u00020\bJ\u0012\u0010\u0014\u001a\u00020\u00052\b\b\u0001\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u0004\u0018\u00010\u0006J\u000e\u0010\u0018\u001a\u00020\u000e2\u0006\u0010\u0019\u001a\u00020\bJ\u001a\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u00062\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0016J\u0010\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0006H\u0016J\u0010\u0010\u001f\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0006H\u0016J\u0010\u0010 \u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0006H\u0016J\u0018\u0010!\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u0010\"\u001a\u00020\u001dH\u0016J\u0010\u0010#\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0006H\u0016J\u0010\u0010$\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0006H\u0016J\u0010\u0010%\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0006H\u0002J\u0010\u0010&\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0006H\u0002R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/example/blogsystem/utils/ActivityManager;", "Landroid/app/Application$ActivityLifecycleCallbacks;", "()V", "mActivityTaskMap", "Landroidx/collection/ArrayMap;", "", "Landroid/app/Activity;", "mApplication", "Landroid/app/Application;", "mLastInvisibleTag", "mLastVisibleTag", "appIsForeground", "", "finishAllActivities", "", "clazzArray", "", "Ljava/lang/Class;", "([Ljava/lang/Class;)V", "getApplication", "getObjectTag", "any", "", "getStackTopActivity", "init", "application", "onActivityCreated", "activity", "savedInstanceState", "Landroid/os/Bundle;", "onActivityDestroyed", "onActivityPaused", "onActivityResumed", "onActivitySaveInstanceState", "outState", "onActivityStarted", "onActivityStopped", "setLastInvisibleTag", "setLastVisibleTag", "app_debug"})
public final class ActivityManager implements android.app.Application.ActivityLifecycleCallbacks {
    private static android.app.Application mApplication;
    private static final androidx.collection.ArrayMap<java.lang.String, android.app.Activity> mActivityTaskMap = null;
    private static java.lang.String mLastVisibleTag;
    private static java.lang.String mLastInvisibleTag;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.utils.ActivityManager INSTANCE = null;
    
    /**
     * 初始化，仅第一次有效
     */
    public final void init(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
    }
    
    /**
     * 获取 Application 对象
     */
    @org.jetbrains.annotations.NotNull()
    public final android.app.Application getApplication() {
        return null;
    }
    
    /**
     * 最后一个可见的Activity和最后一个不可见的Activity不是同一个（换言之：是同一个 Activity，app则未处于前台），
     * 或栈顶 Activity不为null，则app处于前台
     */
    public final boolean appIsForeground() {
        return false;
    }
    
    /**
     * 获取在栈顶的 Activity
     */
    @org.jetbrains.annotations.Nullable()
    public final android.app.Activity getStackTopActivity() {
        return null;
    }
    
    /**
     * 销毁所有 Activity
     */
    public final void finishAllActivities() {
    }
    
    /**
     * 销毁除了参数列表中以外的所有 Activity
     */
    public final void finishAllActivities(@org.jetbrains.annotations.NotNull()
    java.lang.Class<? extends android.app.Activity>... clazzArray) {
    }
    
    @java.lang.Override()
    public void onActivityCreated(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onActivityStarted(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    @java.lang.Override()
    public void onActivityResumed(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    @java.lang.Override()
    public void onActivityPaused(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    @java.lang.Override()
    public void onActivityStopped(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    @java.lang.Override()
    public void onActivitySaveInstanceState(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity, @org.jetbrains.annotations.NotNull()
    android.os.Bundle outState) {
    }
    
    @java.lang.Override()
    public void onActivityDestroyed(@org.jetbrains.annotations.NotNull()
    android.app.Activity activity) {
    }
    
    private final void setLastInvisibleTag(android.app.Activity activity) {
    }
    
    private final void setLastVisibleTag(android.app.Activity activity) {
    }
    
    /**
     * 对象所在的包名 + 对象的内存地址
     */
    private final java.lang.String getObjectTag(@androidx.annotation.NonNull()
    java.lang.Object any) {
        return null;
    }
    
    private ActivityManager() {
        super();
    }
}