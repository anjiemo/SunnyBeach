package com.example.blogsystem.ui.activity;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000e\u001a\u00020\rH\u0002J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016J\b\u0010\u0012\u001a\u00020\u0010H\u0016J\u0012\u0010\u0013\u001a\u00020\u00102\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/example/blogsystem/ui/activity/GalleryActivity;", "Lcom/example/blogsystem/base/BaseActivity;", "()V", "mBinding", "Lcom/example/blogsystem/databinding/ActivityGalleryBinding;", "mPhotoAdapter", "Lcom/example/blogsystem/adapter/PhotoAdapter;", "getMPhotoAdapter", "()Lcom/example/blogsystem/adapter/PhotoAdapter;", "mPhotoAdapter$delegate", "Lkotlin/Lazy;", "mPhotoList", "Ljava/util/ArrayList;", "Lcom/example/blogsystem/network/model/HomePhotoBean$Res$Vertical;", "getCurrentVerticalPhotoBean", "initData", "", "initEvent", "initView", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_debug"})
public final class GalleryActivity extends com.example.blogsystem.base.BaseActivity {
    private com.example.blogsystem.databinding.ActivityGalleryBinding mBinding;
    private final kotlin.Lazy mPhotoAdapter$delegate = null;
    private final java.util.ArrayList<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical> mPhotoList = null;
    private static int mCurrentPage = 0;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.ui.activity.GalleryActivity.Companion Companion = null;
    
    private final com.example.blogsystem.adapter.PhotoAdapter getMPhotoAdapter() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void initEvent() {
    }
    
    private final com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical getCurrentVerticalPhotoBean() {
        return null;
    }
    
    @java.lang.Override()
    public void initData() {
    }
    
    @java.lang.Override()
    public void initView() {
    }
    
    public GalleryActivity() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/example/blogsystem/ui/activity/GalleryActivity$Companion;", "", "()V", "mCurrentPage", "", "startActivity", "", "context", "Landroid/content/Context;", "id", "", "app_debug"})
    public static final class Companion {
        
        public final void startActivity(@org.jetbrains.annotations.NotNull()
        android.content.Context context, @org.jetbrains.annotations.NotNull()
        java.lang.String id) {
        }
        
        private Companion() {
            super();
        }
    }
}