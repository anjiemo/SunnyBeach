package com.example.blogsystem.viewmodel;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bJ\u0010\u0010\n\u001a\u0004\u0018\u00010\t2\u0006\u0010\u000b\u001a\u00020\fJ\u0014\u0010\r\u001a\u00020\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\bR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lcom/example/blogsystem/viewmodel/CookiesViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "cookieDao", "Lcom/example/blogsystem/db/dao/CookieDao;", "getCookies", "", "Lcom/example/blogsystem/utils/CookieStore;", "getCookiesByHost", "host", "", "save", "Lkotlinx/coroutines/Job;", "cookieStoreSet", "app_debug"})
public final class CookiesViewModel extends androidx.lifecycle.AndroidViewModel {
    private final com.example.blogsystem.db.dao.CookieDao cookieDao = null;
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job save(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.blogsystem.utils.CookieStore> cookieStoreSet) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.example.blogsystem.utils.CookieStore getCookiesByHost(@org.jetbrains.annotations.NotNull()
    java.lang.String host) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.example.blogsystem.utils.CookieStore> getCookies() {
        return null;
    }
    
    public CookiesViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
}