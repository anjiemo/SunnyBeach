package com.example.blogsystem.ui.activity;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 )2\u00020\u0001:\u0001)B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0011H\u0016J\b\u0010\u0013\u001a\u00020\u0011H\u0002J\"\u0010\u0014\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00162\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H\u0014J\"\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00162\b\u0010\u001b\u001a\u0004\u0018\u00010\u0019H\u0003J\u0012\u0010\u001c\u001a\u00020\u00112\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\b\u0010\u001f\u001a\u00020\u0011H\u0014J\u001a\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u00162\b\u0010#\u001a\u0004\u0018\u00010$H\u0016J\b\u0010%\u001a\u00020\u0011H\u0002J\u0012\u0010&\u001a\u0004\u0018\u00010\u00062\u0006\u0010\'\u001a\u00020(H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\r\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u000f\u0018\u00010\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006*"}, d2 = {"Lcom/example/blogsystem/ui/activity/BrowserActivity;", "Lcom/example/blogsystem/base/BaseActivity;", "()V", "binding", "Lcom/example/blogsystem/databinding/ActivityBrowserBinding;", "mHeadImgUrl", "", "mNickName", "mOpenId", "mTitle", "mUrl", "uploadMessage", "Lcom/tencent/smtt/sdk/ValueCallback;", "Landroid/net/Uri;", "uploadMessageAboveL", "", "initEvent", "", "initView", "initWebClient", "onActivityResult", "requestCode", "", "resultCode", "data", "Landroid/content/Intent;", "onActivityResultAboveL", "intent", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onKeyDown", "", "keyCode", "event", "Landroid/view/KeyEvent;", "openImageChooserActivity", "parseQrImage", "result", "Lcom/tencent/smtt/sdk/WebView$HitTestResult;", "Companion", "app_debug"})
public final class BrowserActivity extends com.example.blogsystem.base.BaseActivity {
    private com.example.blogsystem.databinding.ActivityBrowserBinding binding;
    private java.lang.String mTitle = "";
    private java.lang.String mUrl = "";
    private com.tencent.smtt.sdk.ValueCallback<android.net.Uri> uploadMessage;
    private com.tencent.smtt.sdk.ValueCallback<android.net.Uri[]> uploadMessageAboveL;
    private java.lang.String mOpenId;
    private java.lang.String mNickName = "default_feedback_nick_name";
    private java.lang.String mHeadImgUrl;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String STR_TITLE = "title";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String STR_URL = "url";
    public static final int FILE_CHOOSER_RESULT_CODE = 10000;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.ui.activity.BrowserActivity.Companion Companion = null;
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void initEvent() {
    }
    
    private final java.lang.String parseQrImage(com.tencent.smtt.sdk.WebView.HitTestResult result) throws java.lang.Exception {
        return null;
    }
    
    @java.lang.Override()
    public void initView() {
    }
    
    private final void initWebClient() {
    }
    
    private final void openImageChooserActivity() {
    }
    
    @java.lang.Override()
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable()
    android.content.Intent data) {
    }
    
    @android.annotation.TargetApi(value = android.os.Build.VERSION_CODES.LOLLIPOP)
    private final void onActivityResultAboveL(int requestCode, int resultCode, android.content.Intent intent) {
    }
    
    /**
     * 拦截back键，改为切换到上一次浏览的网页
     *
     * @param keyCode
     * @param event
     * @return
     */
    @java.lang.Override()
    public boolean onKeyDown(int keyCode, @org.jetbrains.annotations.Nullable()
    android.view.KeyEvent event) {
        return false;
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    public BrowserActivity() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J:\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u00062\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/example/blogsystem/ui/activity/BrowserActivity$Companion;", "", "()V", "FILE_CHOOSER_RESULT_CODE", "", "STR_TITLE", "", "STR_URL", "startActivity", "", "context", "Landroid/content/Context;", "url", "title", "openId", "nickName", "avatar", "app_debug"})
    public static final class Companion {
        
        public final void startActivity(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        java.lang.String url, @org.jetbrains.annotations.Nullable()
        java.lang.String title, @org.jetbrains.annotations.NotNull()
        java.lang.String openId, @org.jetbrains.annotations.NotNull()
        java.lang.String nickName, @org.jetbrains.annotations.NotNull()
        java.lang.String avatar) {
        }
        
        private Companion() {
            super();
        }
    }
}