package com.example.blogsystem.db;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0007J\u0016\u0010\n\u001a\u00020\u00042\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\fH\u0007J\u0010\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u0004H\u0007J\u0010\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0004H\u0007J\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\f2\u0006\u0010\u000e\u001a\u00020\u0004H\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/example/blogsystem/db/Converters;", "", "()V", "cookieStoreToJson", "", "cookieStore", "Lcom/example/blogsystem/utils/CookieStore;", "cookieToJson", "cookie", "Lokhttp3/Cookie;", "cookiesToJson", "cookies", "", "jsonFromCookie", "json", "jsonFromCookieStore", "jsonFromCookies", "app_debug"})
public final class Converters {
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.TypeConverter()
    public final java.lang.String cookieStoreToJson(@org.jetbrains.annotations.NotNull()
    com.example.blogsystem.utils.CookieStore cookieStore) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.TypeConverter()
    public final com.example.blogsystem.utils.CookieStore jsonFromCookieStore(@org.jetbrains.annotations.NotNull()
    java.lang.String json) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.TypeConverter()
    public final java.lang.String cookieToJson(@org.jetbrains.annotations.NotNull()
    okhttp3.Cookie cookie) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.TypeConverter()
    public final okhttp3.Cookie jsonFromCookie(@org.jetbrains.annotations.NotNull()
    java.lang.String json) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.TypeConverter()
    public final java.lang.String cookiesToJson(@org.jetbrains.annotations.NotNull()
    java.util.List<okhttp3.Cookie> cookies) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.TypeConverter()
    public final java.util.List<okhttp3.Cookie> jsonFromCookies(@org.jetbrains.annotations.NotNull()
    java.lang.String json) {
        return null;
    }
    
    public Converters() {
        super();
    }
}