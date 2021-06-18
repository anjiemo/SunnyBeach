package com.example.blogsystem.base;

import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\b\u0010\u0004\u001a\u00020\u0003H\u0016J\b\u0010\u0005\u001a\u00020\u0003H\u0016J\b\u0010\u0006\u001a\u00020\u0003H\u0016J\b\u0010\u0007\u001a\u00020\u0003H\u0016\u00a8\u0006\b"}, d2 = {"Lcom/example/blogsystem/base/Init;", "", "callAllInit", "", "initData", "initEvent", "initSDK", "initView", "app_debug"})
public abstract interface Init {
    
    public abstract void callAllInit();
    
    public abstract void initSDK();
    
    public abstract void initView();
    
    public abstract void initData();
    
    public abstract void initEvent();
    
    @kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3)
    public final class DefaultImpls {
        
        public static void callAllInit(@org.jetbrains.annotations.NotNull()
        com.example.blogsystem.base.Init $this) {
        }
        
        public static void initSDK(@org.jetbrains.annotations.NotNull()
        com.example.blogsystem.base.Init $this) {
        }
        
        public static void initView(@org.jetbrains.annotations.NotNull()
        com.example.blogsystem.base.Init $this) {
        }
        
        public static void initData(@org.jetbrains.annotations.NotNull()
        com.example.blogsystem.base.Init $this) {
        }
        
        public static void initEvent(@org.jetbrains.annotations.NotNull()
        com.example.blogsystem.base.Init $this) {
        }
    }
}