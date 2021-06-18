package com.example.blogsystem.db.dao;

import java.lang.System;

@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\'J\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001f\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000b\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\f"}, d2 = {"Lcom/example/blogsystem/db/dao/CookieDao;", "", "getCookies", "", "Lcom/example/blogsystem/utils/CookieStore;", "getCookiesByHost", "host", "", "save", "", "cookieStoreSet", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface CookieDao {
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    @androidx.room.Transaction()
    public abstract java.lang.Object save(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.blogsystem.utils.CookieStore> cookieStoreSet, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> p1);
    
    @org.jetbrains.annotations.Nullable()
    @androidx.room.Query(value = "SELECT * FROM tb_cookies WHERE host = :host")
    @androidx.room.Transaction()
    public abstract com.example.blogsystem.utils.CookieStore getCookiesByHost(@org.jetbrains.annotations.NotNull()
    java.lang.String host);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM tb_cookies")
    @androidx.room.Transaction()
    public abstract java.util.List<com.example.blogsystem.utils.CookieStore> getCookies();
}