package com.example.blogsystem.viewmodel.login;

import java.lang.System;

/**
 * 登录表单的数据验证状态
 */
@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000bJ\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000bJ\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0007H\u00c6\u0003J<\u0010\u0013\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\u0014J\u0013\u0010\u0015\u001a\u00020\u00072\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\tR\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0015\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\r\u0010\u000bR\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\n\n\u0002\u0010\f\u001a\u0004\b\u000e\u0010\u000b\u00a8\u0006\u001a"}, d2 = {"Lcom/example/blogsystem/viewmodel/login/LoginFormState;", "", "userAccountError", "", "passwordError", "verifyError", "isDataValid", "", "(Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Z)V", "()Z", "getPasswordError", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getUserAccountError", "getVerifyError", "component1", "component2", "component3", "component4", "copy", "(Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;Z)Lcom/example/blogsystem/viewmodel/login/LoginFormState;", "equals", "other", "hashCode", "toString", "", "app_debug"})
public final class LoginFormState {
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer userAccountError = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer passwordError = null;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Integer verifyError = null;
    private final boolean isDataValid = false;
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getUserAccountError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getPasswordError() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getVerifyError() {
        return null;
    }
    
    public final boolean isDataValid() {
        return false;
    }
    
    public LoginFormState(@org.jetbrains.annotations.Nullable()
    java.lang.Integer userAccountError, @org.jetbrains.annotations.Nullable()
    java.lang.Integer passwordError, @org.jetbrains.annotations.Nullable()
    java.lang.Integer verifyError, boolean isDataValid) {
        super();
    }
    
    public LoginFormState() {
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
    public final java.lang.Integer component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    /**
     * 登录表单的数据验证状态
     */
    @org.jetbrains.annotations.NotNull()
    public final com.example.blogsystem.viewmodel.login.LoginFormState copy(@org.jetbrains.annotations.Nullable()
    java.lang.Integer userAccountError, @org.jetbrains.annotations.Nullable()
    java.lang.Integer passwordError, @org.jetbrains.annotations.Nullable()
    java.lang.Integer verifyError, boolean isDataValid) {
        return null;
    }
    
    /**
     * 登录表单的数据验证状态
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    /**
     * 登录表单的数据验证状态
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * 登录表单的数据验证状态
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object p0) {
        return false;
    }
}