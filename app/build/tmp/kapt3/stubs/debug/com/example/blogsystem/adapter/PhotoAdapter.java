package com.example.blogsystem.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u00012\u00020\u0004B\u000f\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\u0018\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0002H\u0014J>\u0010\u0016\u001a\u00020\u001126\u0010\u0017\u001a2\u0012\u0013\u0012\u00110\u0002\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u00110\u000bJ>\u0010\u0018\u001a\u00020\u001126\u0010\u0017\u001a2\u0012\u0013\u0012\u00110\u0002\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u00110\u000bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR>\u0010\n\u001a2\u0012\u0013\u0012\u00110\u0002\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u00110\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R>\u0010\u0012\u001a2\u0012\u0013\u0012\u00110\u0002\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u00110\u000bX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/example/blogsystem/adapter/PhotoAdapter;", "Lcom/chad/library/adapter/base/BaseQuickAdapter;", "Lcom/example/blogsystem/network/model/HomePhotoBean$Res$Vertical;", "Lcom/chad/library/adapter/base/viewholder/BaseViewHolder;", "Lcom/chad/library/adapter/base/module/LoadMoreModule;", "fillBox", "", "(Z)V", "getFillBox", "()Z", "mItemClickListener", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "verticalPhoto", "", "position", "", "mItemLongClickListener", "convert", "holder", "item", "setOnItemClickListener", "listener", "setOnItemLongClickListener", "app_debug"})
public final class PhotoAdapter extends com.chad.library.adapter.base.BaseQuickAdapter<com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical, com.chad.library.adapter.base.viewholder.BaseViewHolder> implements com.chad.library.adapter.base.module.LoadMoreModule {
    private kotlin.jvm.functions.Function2<? super com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical, ? super java.lang.Integer, kotlin.Unit> mItemClickListener;
    private kotlin.jvm.functions.Function2<? super com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical, ? super java.lang.Integer, kotlin.Unit> mItemLongClickListener;
    private final boolean fillBox = false;
    
    public final void setOnItemClickListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical, ? super java.lang.Integer, kotlin.Unit> listener) {
    }
    
    public final void setOnItemLongClickListener(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical, ? super java.lang.Integer, kotlin.Unit> listener) {
    }
    
    @java.lang.Override()
    protected void convert(@org.jetbrains.annotations.NotNull()
    com.chad.library.adapter.base.viewholder.BaseViewHolder holder, @org.jetbrains.annotations.NotNull()
    com.example.blogsystem.network.model.HomePhotoBean.Res.Vertical item) {
    }
    
    public final boolean getFillBox() {
        return false;
    }
    
    public PhotoAdapter(boolean fillBox) {
        super(0, null);
    }
    
    public PhotoAdapter() {
        super(0, null);
    }
}