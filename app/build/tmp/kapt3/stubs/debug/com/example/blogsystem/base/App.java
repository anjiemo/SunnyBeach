package com.example.blogsystem.base;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\b\u0010\u0010\u001a\u00020\u000fH\u0016J\b\u0010\u0011\u001a\u00020\u000fH\u0016R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0013"}, d2 = {"Lcom/example/blogsystem/base/App;", "Landroid/app/Application;", "()V", "appViewModel", "Lcom/example/blogsystem/viewmodel/app/AppViewModel;", "getAppViewModel", "()Lcom/example/blogsystem/viewmodel/app/AppViewModel;", "appViewModel$delegate", "Lkotlin/Lazy;", "database", "Lcom/example/blogsystem/db/CookieRoomDatabase;", "getDatabase", "()Lcom/example/blogsystem/db/CookieRoomDatabase;", "database$delegate", "initApp", "", "onCreate", "onLowMemory", "Companion", "app_debug"})
public final class App extends android.app.Application {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy database$delegate = null;
    private final kotlin.Lazy appViewModel$delegate = null;
    private static com.example.blogsystem.base.App mApp;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.base.App.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.blogsystem.db.CookieRoomDatabase getDatabase() {
        return null;
    }
    
    private final com.example.blogsystem.viewmodel.app.AppViewModel getAppViewModel() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    private final void initApp() {
    }
    
    @java.lang.Override()
    public void onLowMemory() {
    }
    
    public App() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/example/blogsystem/base/App$Companion;", "", "()V", "mApp", "Lcom/example/blogsystem/base/App;", "get", "app_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.blogsystem.base.App get() {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}