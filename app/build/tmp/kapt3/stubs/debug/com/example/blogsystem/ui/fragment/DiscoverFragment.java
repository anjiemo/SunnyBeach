package com.example.blogsystem.ui.fragment;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\b\u0010\u001a\u001a\u00020\u0019H\u0016J\b\u0010\u001b\u001a\u00020\u0019H\u0016J\u001a\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u0016J\u0010\u0010!\u001a\u00020\u00192\u0006\u0010\"\u001a\u00020#H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u001b\u0010\u0011\u001a\u00020\u00128BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0015\u0010\n\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lcom/example/blogsystem/ui/fragment/DiscoverFragment;", "Lcom/example/blogsystem/base/BaseFragment;", "()V", "_binding", "Lcom/example/blogsystem/databinding/FragmentDiscoverBinding;", "discoverViewModel", "Lcom/example/blogsystem/viewmodel/discover/DiscoverViewModel;", "getDiscoverViewModel", "()Lcom/example/blogsystem/viewmodel/discover/DiscoverViewModel;", "discoverViewModel$delegate", "Lkotlin/Lazy;", "mBannerList", "Ljava/util/ArrayList;", "Lcom/example/blogsystem/network/model/HomeBannerBean$Data;", "mBinding", "getMBinding", "()Lcom/example/blogsystem/databinding/FragmentDiscoverBinding;", "mPhotoAdapter", "Lcom/example/blogsystem/adapter/PhotoAdapter;", "getMPhotoAdapter", "()Lcom/example/blogsystem/adapter/PhotoAdapter;", "mPhotoAdapter$delegate", "mPhotoList", "Lcom/example/blogsystem/network/model/HomePhotoBean$Res$Vertical;", "initData", "", "initEvent", "initView", "onViewCreated", "view", "Landroid/view/View;", "savedInstanceState", "Landroid/os/Bundle;", "setupSpacing", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "app_debug"})
public final class DiscoverFragment extends com.example.blogsystem.base.BaseFragment {
    private com.example.blogsystem.databinding.FragmentDiscoverBinding _binding;
    private final java.util.ArrayList<com.example.blogsystem.network.model.HomeBannerBean.Data> mBannerList = null;
    private final kotlin.Lazy mPhotoAdapter$delegate = null;
    private final java.util.ArrayList<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical> mPhotoList = null;
    private final kotlin.Lazy discoverViewModel$delegate = null;
    
    private final com.example.blogsystem.databinding.FragmentDiscoverBinding getMBinding() {
        return null;
    }
    
    private final com.example.blogsystem.adapter.PhotoAdapter getMPhotoAdapter() {
        return null;
    }
    
    private final com.example.blogsystem.viewmodel.discover.DiscoverViewModel getDiscoverViewModel() {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void initEvent() {
    }
    
    @java.lang.Override()
    public void initData() {
    }
    
    @java.lang.Override()
    public void initView() {
    }
    
    private final void setupSpacing(androidx.recyclerview.widget.RecyclerView recyclerView) {
    }
    
    public DiscoverFragment() {
        super();
    }
}