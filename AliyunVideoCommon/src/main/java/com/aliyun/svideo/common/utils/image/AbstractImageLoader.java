package com.aliyun.svideo.common.utils.image;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 1.图片库设计 -> com.aliyun.svideo.common.utils.image
 * 目标1：切换图片库的时候不需要改动接口
 * 目标2：图片库升级的时候只需要改动实现类的代码
 * <p>
 * 2.核心类
 * 1.AbstractImageLoader 抽象类接口
 * 2.AbstractImageLoaderTarget 图片库加载Target接口
 * 3.ImageLoaderImpl 图片库功能实现类->基于Glide
 * 4.ImageLoaderOptions 图片库参数构建类
 * 5.ImageLoaderRequestListener 图片加载监听接口
 */
public abstract class AbstractImageLoader {

    /**
     * 使用默认配置
     */
    public abstract AbstractImageLoader loadImage(@NonNull Context context, @NonNull String url);

    /**
     * 使用默认配置
     */
    public abstract AbstractImageLoader loadImage(@NonNull Context context, @NonNull int resId);

    /**
     * 加载图片
     *
     * @param url           图片地址/uri
     * @param loaderOptions ImageLoaderOptions
     * @return this
     */
    public abstract AbstractImageLoader loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageLoaderOptions loaderOptions);

    /**
     * 加载图片
     *
     * @param resId         图片资源id
     * @param loaderOptions ImageLoaderOptions
     * @return this
     */
    public abstract AbstractImageLoader loadImage(@NonNull Context context, @DrawableRes int resId, @Nullable ImageLoaderOptions loaderOptions);

    /**
     * 添加listener
     *
     * @param listener RequestListener
     */
    public abstract <R> AbstractImageLoader listener(@NonNull ImageLoaderRequestListener<R> listener);

    /**
     * Sets the {@link ImageView} the resource will be loaded into, cancels any existing loads into
     * the view, and frees any resources Glide may have previously loaded into the view so they may be
     * reused.
     *
     * @param imageView ImageView
     */
    public abstract void into(@NonNull ImageView imageView);

    /**
     * Set the target the resource will be loaded into.
     *
     * @param loaderTarget The target to load the resource into.
     * @param view         目标view对象,用于匹配加载资源Target的参数相关因素，并不会直接设置资源的显示
     */
    public abstract <T> void into(@NonNull View view, @NonNull final AbstractImageLoaderTarget<T> loaderTarget);

    /**
     * 减少引用计数
     *
     * @param context   Context
     * @param imageView imageView
     */
    public abstract void clear(@NonNull Context context, @NonNull ImageView imageView);

}
