package com.example.blogsystem.adapter;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0014B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0007\u001a\u00020\bH\u0016J\u001c\u0010\t\u001a\u00020\n2\n\u0010\u000b\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\f\u001a\u00020\bH\u0016J\u001c\u0010\r\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u0014\u0010\u0011\u001a\u00020\n2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0013R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/example/blogsystem/adapter/ArticleAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/blogsystem/adapter/ArticleAdapter$Holder;", "()V", "mData", "Ljava/util/ArrayList;", "Lcom/example/blogsystem/model/ArticleInfo$ArticleItem;", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "setData", "data", "", "Holder", "app_debug"})
public final class ArticleAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.blogsystem.adapter.ArticleAdapter.Holder> {
    private final java.util.ArrayList<com.example.blogsystem.model.ArticleInfo.ArticleItem> mData = null;
    
    public final void setData(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.blogsystem.model.ArticleInfo.ArticleItem> data) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public com.example.blogsystem.adapter.ArticleAdapter.Holder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.example.blogsystem.adapter.ArticleAdapter.Holder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public ArticleAdapter() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/example/blogsystem/adapter/ArticleAdapter$Holder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/example/blogsystem/databinding/ItemArticleBinding;", "(Lcom/example/blogsystem/adapter/ArticleAdapter;Lcom/example/blogsystem/databinding/ItemArticleBinding;)V", "getBinding", "()Lcom/example/blogsystem/databinding/ItemArticleBinding;", "app_debug"})
    public final class Holder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.example.blogsystem.databinding.ItemArticleBinding binding = null;
        
        @org.jetbrains.annotations.NotNull()
        public final com.example.blogsystem.databinding.ItemArticleBinding getBinding() {
            return null;
        }
        
        public Holder(@org.jetbrains.annotations.NotNull()
        com.example.blogsystem.databinding.ItemArticleBinding binding) {
            super(null);
        }
    }
}