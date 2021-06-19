package com.example.blogsystem.viewmodel;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010 \u001a\u00020!J\u0006\u0010\"\u001a\u00020!J\n\u0010\u001f\u001a\u0004\u0018\u00010\rH\u0002J\u0010\u0010#\u001a\u00020\u00072\u0006\u0010$\u001a\u00020%H\u0002J\u0010\u0010&\u001a\u00020\u00072\u0006\u0010\'\u001a\u00020%H\u0002J\u0010\u0010(\u001a\u00020\u00072\u0006\u0010)\u001a\u00020%H\u0002J\u001e\u0010*\u001a\u00020!2\u0006\u0010\'\u001a\u00020%2\u0006\u0010$\u001a\u00020%2\u0006\u0010+\u001a\u00020%J\u001e\u0010,\u001a\u00020-2\u0006\u0010\'\u001a\u00020%2\u0006\u0010$\u001a\u00020%2\u0006\u0010+\u001a\u00020%J\u0010\u0010.\u001a\u00020-2\u0006\u0010\u001e\u001a\u00020\rH\u0002R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\t0\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0011R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000b0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011R\u001b\u0010\u0018\u001a\u00020\u00198BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001c\u0010\u001d\u001a\u0004\b\u001a\u0010\u001bR\u0017\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\r0\u000f8F\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010\u0011\u00a8\u0006/"}, d2 = {"Lcom/example/blogsystem/viewmodel/UserViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_autoLogin", "Landroidx/lifecycle/MutableLiveData;", "", "_loginForm", "Lcom/example/blogsystem/viewmodel/login/LoginFormState;", "_loginResult", "Lcom/example/blogsystem/viewmodel/login/LoginResult;", "_userBasicInfo", "Lcom/example/blogsystem/model/UserBasicInfo;", "autoLogin", "Landroidx/lifecycle/LiveData;", "getAutoLogin", "()Landroidx/lifecycle/LiveData;", "cookieDao", "Lcom/example/blogsystem/db/dao/CookieDao;", "loginFormState", "getLoginFormState", "loginResult", "getLoginResult", "userApi", "Lcom/example/blogsystem/network/api/UserApi;", "getUserApi", "()Lcom/example/blogsystem/network/api/UserApi;", "userApi$delegate", "Lkotlin/Lazy;", "userBasicInfo", "getUserBasicInfo", "checkUserState", "Lkotlinx/coroutines/Job;", "checkUserToken", "isPasswordValid", "password", "", "isUserAccountValid", "userAccount", "isVerifyCodeValid", "verifyCode", "login", "captcha", "loginDataChanged", "", "saveUserBasicInfo", "app_debug"})
public final class UserViewModel extends androidx.lifecycle.AndroidViewModel {
    private final com.example.blogsystem.db.dao.CookieDao cookieDao = null;
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> _autoLogin = null;
    private final androidx.lifecycle.MutableLiveData<com.example.blogsystem.viewmodel.login.LoginFormState> _loginForm = null;
    private final androidx.lifecycle.MutableLiveData<com.example.blogsystem.viewmodel.login.LoginResult> _loginResult = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<com.example.blogsystem.viewmodel.login.LoginResult> loginResult = null;
    private final androidx.lifecycle.MutableLiveData<com.example.blogsystem.model.UserBasicInfo> _userBasicInfo = null;
    private final kotlin.Lazy userApi$delegate = null;
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> getAutoLogin() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.blogsystem.viewmodel.login.LoginFormState> getLoginFormState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.blogsystem.viewmodel.login.LoginResult> getLoginResult() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.example.blogsystem.model.UserBasicInfo> getUserBasicInfo() {
        return null;
    }
    
    private final com.example.blogsystem.network.api.UserApi getUserApi() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job login(@org.jetbrains.annotations.NotNull()
    java.lang.String userAccount, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String captcha) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job checkUserToken() {
        return null;
    }
    
    public final void loginDataChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String userAccount, @org.jetbrains.annotations.NotNull()
    java.lang.String password, @org.jetbrains.annotations.NotNull()
    java.lang.String captcha) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job checkUserState() {
        return null;
    }
    
    /**
     * 保存用户基本信息
     */
    private final void saveUserBasicInfo(com.example.blogsystem.model.UserBasicInfo userBasicInfo) {
    }
    
    /**
     * 获取用户基本信息
     */
    private final com.example.blogsystem.model.UserBasicInfo getUserBasicInfo() {
        return null;
    }
    
    /**
     * 手机号码格式检查
     */
    private final boolean isUserAccountValid(java.lang.String userAccount) {
        return false;
    }
    
    /**
     * 账号密码长度检查
     */
    private final boolean isPasswordValid(java.lang.String password) {
        return false;
    }
    
    /**
     * 验证码检查
     */
    private final boolean isVerifyCodeValid(java.lang.String verifyCode) {
        return false;
    }
    
    public UserViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
}