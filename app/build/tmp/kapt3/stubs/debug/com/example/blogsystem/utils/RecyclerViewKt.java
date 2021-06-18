package com.example.blogsystem.utils;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 2, d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a&\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t\u001a&\u0010\n\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t\u001a\u000e\u0010\u000b\u001a\u0004\u0018\u00010\f*\u00020\tH\u0002\u001a\u000e\u0010\r\u001a\u0004\u0018\u00010\u000e*\u00020\tH\u0002\u001a\n\u0010\u000f\u001a\u00020\u0003*\u00020\t\u001a\n\u0010\u0010\u001a\u00020\u0003*\u00020\t\u00a8\u0006\u0011"}, d2 = {"equilibriumAssignmentOfGrid", "", "unit", "", "outRect", "Landroid/graphics/Rect;", "view", "Landroid/view/View;", "parent", "Landroidx/recyclerview/widget/RecyclerView;", "equilibriumAssignmentOfLinear", "checkGridLayoutManager", "Landroidx/recyclerview/widget/GridLayoutManager;", "checkLinearLayoutManager", "Landroidx/recyclerview/widget/LinearLayoutManager;", "getItemCount", "getSpanCount", "app_debug"})
public final class RecyclerViewKt {
    
    /**
     * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView)} or
     * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView,state: RecyclerView.State)}.
     * 均分 LinearLayoutManager 间距的便捷方法
     */
    public static final void equilibriumAssignmentOfLinear(int unit, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect outRect, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView parent) {
    }
    
    /**
     * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView)} or
     * {@link ItemDecoration#getItemOffsets(outRect: Rect,view: View,parent: RecyclerView,state: RecyclerView.State)}.
     * 均分 GridLayoutManager 间距的便捷方法
     */
    public static final void equilibriumAssignmentOfGrid(int unit, @org.jetbrains.annotations.NotNull()
    android.graphics.Rect outRect, @org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView parent) {
    }
    
    /**
     * 获取 spanCount
     * 注：此方法只针对设置 LayoutManager 为 GridLayoutManager 的 RecyclerView 生效
     */
    public static final int getSpanCount(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView $this$getSpanCount) {
        return 0;
    }
    
    /**
     * 返回绑定到父 RecyclerView 的适配器中的项目数
     */
    public static final int getItemCount(@org.jetbrains.annotations.NotNull()
    androidx.recyclerview.widget.RecyclerView $this$getItemCount) {
        return 0;
    }
    
    /**
     * 检查 RecyclerView 设置的 LinearLayoutManager
     */
    private static final androidx.recyclerview.widget.LinearLayoutManager checkLinearLayoutManager(androidx.recyclerview.widget.RecyclerView $this$checkLinearLayoutManager) {
        return null;
    }
    
    /**
     * 检查 RecyclerView 设置的 GridLayoutManager
     */
    private static final androidx.recyclerview.widget.GridLayoutManager checkGridLayoutManager(androidx.recyclerview.widget.RecyclerView $this$checkGridLayoutManager) {
        return null;
    }
}