package com.example.blogsystem.ui.activity;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010$\u001a\u00020%H\u0016J\b\u0010&\u001a\u00020%H\u0016J\b\u0010\'\u001a\u00020%H\u0016J\u0012\u0010(\u001a\u00020%2\b\u0010)\u001a\u0004\u0018\u00010*H\u0014J\u0006\u0010+\u001a\u00020%R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u001b\u0010\u000b\u001a\u00020\f8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000f\u0010\n\u001a\u0004\b\r\u0010\u000eR\u001b\u0010\u0010\u001a\u00020\u00118BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0014\u0010\n\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082.\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u001d\u0012\u0004\u0012\u00020\u001e0\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u001f\u001a\u00020 8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b#\u0010\n\u001a\u0004\b!\u0010\"\u00a8\u0006,"}, d2 = {"Lcom/example/blogsystem/ui/activity/HomeActivity;", "Lcom/example/blogsystem/base/BaseActivity;", "()V", "appUpdateDialogBuilder", "Landroidx/appcompat/app/AlertDialog$Builder;", "appUpdateViewModel", "Lcom/example/blogsystem/viewmodel/app/AppViewModel;", "getAppUpdateViewModel", "()Lcom/example/blogsystem/viewmodel/app/AppViewModel;", "appUpdateViewModel$delegate", "Lkotlin/Lazy;", "discoverFragment", "Lcom/example/blogsystem/ui/fragment/DiscoverFragment;", "getDiscoverFragment", "()Lcom/example/blogsystem/ui/fragment/DiscoverFragment;", "discoverFragment$delegate", "homeFragment", "Lcom/example/blogsystem/ui/fragment/HomeFragment;", "getHomeFragment", "()Lcom/example/blogsystem/ui/fragment/HomeFragment;", "homeFragment$delegate", "isCancel", "", "mBinding", "Lcom/example/blogsystem/databinding/ActivityHomeBinding;", "mFragmentAdapter", "Lcom/example/blogsystem/base/FragmentAdapter;", "mFragmentMap", "Landroidx/collection/ArrayMap;", "", "Lcom/example/blogsystem/base/BaseFragment;", "meFragment", "Lcom/example/blogsystem/ui/fragment/MeFragment;", "getMeFragment", "()Lcom/example/blogsystem/ui/fragment/MeFragment;", "meFragment$delegate", "initData", "", "initEvent", "initView", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "showHomeFragment", "app_debug"})
public final class HomeActivity extends com.example.blogsystem.base.BaseActivity {
    private final kotlin.Lazy appUpdateViewModel$delegate = null;
    private com.example.blogsystem.databinding.ActivityHomeBinding mBinding;
    private final androidx.collection.ArrayMap<java.lang.Integer, com.example.blogsystem.base.BaseFragment> mFragmentMap = null;
    private com.example.blogsystem.base.FragmentAdapter mFragmentAdapter;
    private final kotlin.Lazy homeFragment$delegate = null;
    private final kotlin.Lazy discoverFragment$delegate = null;
    private final kotlin.Lazy meFragment$delegate = null;
    private boolean isCancel = false;
    private androidx.appcompat.app.AlertDialog.Builder appUpdateDialogBuilder;
    
    private final com.example.blogsystem.viewmodel.app.AppViewModel getAppUpdateViewModel() {
        return null;
    }
    
    private final com.example.blogsystem.ui.fragment.HomeFragment getHomeFragment() {
        return null;
    }
    
    private final com.example.blogsystem.ui.fragment.DiscoverFragment getDiscoverFragment() {
        return null;
    }
    
    private final com.example.blogsystem.ui.fragment.MeFragment getMeFragment() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void initData() {
    }
    
    public final void showHomeFragment() {
    }
    
    @java.lang.Override()
    public void initView() {
    }
    
    @java.lang.Override()
    public void initEvent() {
    }
    
    public HomeActivity() {
        super();
    }
}