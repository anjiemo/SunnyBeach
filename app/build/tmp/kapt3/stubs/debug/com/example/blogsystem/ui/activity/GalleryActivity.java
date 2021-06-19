package com.example.blogsystem.ui.activity;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u001e2\u00020\u0001:\u0001\u001eB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0016\u001a\u00020\u0012H\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u0018H\u0016J\b\u0010\u001a\u001a\u00020\u0018H\u0016J\u0012\u0010\u001b\u001a\u00020\u00182\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH\u0014R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\b\u001a\u0004\b\r\u0010\u000eR!\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00118BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\b\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\u001f"}, d2 = {"Lcom/example/blogsystem/ui/activity/GalleryActivity;", "Lcom/example/blogsystem/base/BaseActivity;", "()V", "discoverViewModel", "Lcom/example/blogsystem/viewmodel/discover/DiscoverViewModel;", "getDiscoverViewModel", "()Lcom/example/blogsystem/viewmodel/discover/DiscoverViewModel;", "discoverViewModel$delegate", "Lkotlin/Lazy;", "mBinding", "Lcom/example/blogsystem/databinding/ActivityGalleryBinding;", "mPhotoAdapter", "Lcom/example/blogsystem/adapter/PhotoAdapter;", "getMPhotoAdapter", "()Lcom/example/blogsystem/adapter/PhotoAdapter;", "mPhotoAdapter$delegate", "mPhotoList", "Ljava/util/ArrayList;", "Lcom/example/blogsystem/network/model/HomePhotoBean$Res$Vertical;", "getMPhotoList", "()Ljava/util/ArrayList;", "mPhotoList$delegate", "getCurrentVerticalPhotoBean", "initData", "", "initEvent", "initView", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_debug"})
public final class GalleryActivity extends com.example.blogsystem.base.BaseActivity {
    private final kotlin.Lazy discoverViewModel$delegate = null;
    private com.example.blogsystem.databinding.ActivityGalleryBinding mBinding;
    private final kotlin.Lazy mPhotoAdapter$delegate = null;
    private final kotlin.Lazy mPhotoList$delegate = null;
    private static int mCurrentPage = 0;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.blogsystem.ui.activity.GalleryActivity.Companion Companion = null;
    
    private final com.example.blogsystem.viewmodel.discover.DiscoverViewModel getDiscoverViewModel() {
        return null;
    }
    
    private final com.example.blogsystem.adapter.PhotoAdapter getMPhotoAdapter() {
        return null;
    }
    
    private final java.util.ArrayList<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical> getMPhotoList() {
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