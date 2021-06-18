package com.example.blogsystem.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B%\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00028\u0000\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0014\u001a\u00020\u0004H\u00c6\u0003J\u000e\u0010\u0015\u001a\u00028\u0000H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u0016\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\tH\u00c6\u0003J<\u0010\u0018\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00028\u00002\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u00c6\u0001\u00a2\u0006\u0002\u0010\u0019J\u0013\u0010\u001a\u001a\u00020\t2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0002H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u0004H\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0007H\u00d6\u0001R\u0016\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0018\u0010\u0005\u001a\u00028\u00008\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001e"}, d2 = {"Lcom/example/blogsystem/model/BaseResponse;", "T", "", "code", "", "data", "message", "", "success", "", "(ILjava/lang/Object;Ljava/lang/String;Z)V", "getCode", "()I", "getData", "()Ljava/lang/Object;", "Ljava/lang/Object;", "getMessage", "()Ljava/lang/String;", "getSuccess", "()Z", "component1", "component2", "component3", "component4", "copy", "(ILjava/lang/Object;Ljava/lang/String;Z)Lcom/example/blogsystem/model/BaseResponse;", "equals", "other", "hashCode", "toString", "app_debug"})
public final class BaseResponse<T extends java.lang.Object> {
    @com.google.gson.annotations.SerializedName(value = "code")
    private final int code = 0;
    @com.google.gson.annotations.SerializedName(value = "data")
    private final T data = null;
    @org.jetbrains.annotations.NotNull()
    @com.google.gson.annotations.SerializedName(value = "message")
    private final java.lang.String message = null;
    @com.google.gson.annotations.SerializedName(value = "success")
    private final boolean success = false;
    
    public final int getCode() {
        return 0;
    }
    
    public final T getData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getMessage() {
        return null;
    }
    
    public final boolean getSuccess() {
        return false;
    }
    
    public BaseResponse(int code, T data, @org.jetbrains.annotations.NotNull()
    java.lang.String message, boolean success) {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final T component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.blogsystem.model.BaseResponse<T> copy(int code, T data, @org.jetbrains.annotations.NotNull()
    java.lang.String message, boolean success) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}