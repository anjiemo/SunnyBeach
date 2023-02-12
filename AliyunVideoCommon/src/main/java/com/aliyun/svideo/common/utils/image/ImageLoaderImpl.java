package com.aliyun.svideo.common.utils.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * 使用示例
 * <p>
 * 1.无配置参数
 * <p>
 * new ImageLoaderImpl().loadImage(context,url).into(imageView);
 * <p>
 * 2.有配置参数
 * <p>
 * new ImageLoaderImpl().loadImage(context,url,new ImageLoaderOptions.builder()
 * .circle()//加载圆形
 * .crossFade//淡入淡出
 * .build())
 * .into(imageView);
 * <p>
 * 除此之外,还支持listener和viewTarget的加载方式
 * <p>
 * new ImageLoaderImpl().loadImage(context,url)
 * .listener(imageLoaderRequestListener)
 * .into(imageView);
 * <p>
 * new ImageLoaderImpl().loadImage(context,url)
 * .into(view,abstractImageLoaderTarget);
 */
public class ImageLoaderImpl extends AbstractImageLoader {

    private static final String TAG = ImageLoaderImpl.class.getSimpleName();
    /**
     * Glide RequestBuilder
     */
    private RequestBuilder mRequestBuilder;

    /**
     * 加载图片
     *
     * @param context Context
     * @param url     图片地址
     * @return AbstractImageLoader
     */
    @Override
    public AbstractImageLoader loadImage(@NonNull Context context, @NonNull String url) {

        return loadImage(context, url, new ImageLoaderOptions.Builder().build());
    }

    /**
     * 加载图片
     *
     * @param context Context
     * @param resId   资源id
     * @return AbstractImageLoader
     */
    @Override
    public AbstractImageLoader loadImage(@NonNull Context context, @NonNull int resId) {

        return loadImage(context, resId, new ImageLoaderOptions.Builder().build());
    }

    /**
     * 加载图片
     *
     * @param context       Context
     * @param url           图片地址/uri
     * @param loaderOptions ImageLoaderOptions参数配置
     * @return AbstractImageLoader
     */
    @Override
    public AbstractImageLoader loadImage(@NonNull Context context, @NonNull String url, @NonNull ImageLoaderOptions loaderOptions) {

        loadGlideResource(context, url, loaderOptions);
        return this;
    }

    /**
     * 加载图片
     *
     * @param context       Context
     * @param resId         图片资源id
     * @param loaderOptions ImageLoaderOptions
     * @return AbstractImageLoader
     */
    @Override
    public AbstractImageLoader loadImage(@NonNull Context context, int resId, @NonNull ImageLoaderOptions loaderOptions) {

        loadGlideResource(context, resId, loaderOptions);
        return this;
    }

    /**
     * glide加载资源
     *
     * @param context       Context
     * @param resource      string or int
     * @param loaderOptions ImageLoaderOptions
     */
    private void loadGlideResource(@NonNull Context context, Object resource, @NonNull ImageLoaderOptions loaderOptions) {
        RequestManager requestManager;
        if (context instanceof Activity) {
            // 处理bug
            // java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                Log.e(TAG, "You cannot start a load for a destroyed activity");
                return;
            }
            requestManager = Glide.with(activity);
        } else {
            requestManager = Glide.with(context);
        }

        // 在这里淡入淡出对应DrawableTransitionOptions、BitmapTransitionOptions
        // asBitmap对应RequestBuilder<Bitmap>、RequestBuilder<Drawable>
        if (loaderOptions.isAsBitmap()) {
            RequestBuilder<Bitmap> requestBuilder = requestManager.asBitmap().load(resource instanceof String ? ((String) resource) : (
                    (Integer) resource));
            if (loaderOptions.isCrossFade()) {
                requestBuilder = requestBuilder.transition(new BitmapTransitionOptions().crossFade());
            }
            loadGlideOption(context, requestBuilder, loaderOptions);
        } else {
            RequestBuilder<Drawable> requestBuilder = requestManager.load(resource instanceof String ? ((String) resource) : (
                    (Integer) resource));
            if (loaderOptions.isCrossFade()) {
                requestBuilder = requestBuilder.transition(new DrawableTransitionOptions().crossFade());
            }
            loadGlideOption(context, requestBuilder, loaderOptions);
        }
    }

    /**
     * 设置Glide RequestBuilder 参数
     *
     * @param loaderOptions ImageLoaderOptions
     */
    @SuppressLint("CheckResult")
    private <R> void loadGlideOption(Context context, RequestBuilder<R> requestBuilder, ImageLoaderOptions loaderOptions) {
        mRequestBuilder = requestBuilder;
        //是否跳过内存
        RequestOptions requestOptions = RequestOptions.skipMemoryCacheOf(loaderOptions.isSkipMemoryCache());
        //占位符
        if (loaderOptions.getHolderDrawable() != null) {
            requestOptions = requestOptions.placeholder(loaderOptions.getHolderDrawable());
        }
        if (loaderOptions.getHolderDrawableId() != -1) {
            requestOptions = requestOptions.placeholder(loaderOptions.getHolderDrawableId());
        }
        //错误图
        if (loaderOptions.getErrorDrawableId() != -1) {
            requestOptions = requestOptions.error(loaderOptions.getErrorDrawableId());
        }
        //isCenterCrop
        if (loaderOptions.isCenterCrop()) {
            requestOptions = requestOptions.centerCrop();
        }
        //isCircle
        if (loaderOptions.isCircle()) {
            requestOptions = requestOptions.optionalCircleCrop();
        }
        //mIsSkipDiskCacheCache
        if (loaderOptions.isSkipDiskCacheCache()) {
            requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        } else {
            //默认使用智能策略
            requestOptions = requestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        }
        //thumbnail
        if (loaderOptions.getThumbnail() != 1f) {
            mRequestBuilder.thumbnail(loaderOptions.getThumbnail());
        }
        //override
        Point overridePoint = loaderOptions.getOverridePoint();
        if (overridePoint.x != 0 && overridePoint.y != 0) {
            requestOptions = requestOptions.override(overridePoint.x, overridePoint.y);
        }
        //圆角
        if (loaderOptions.isRoundCorner()) {
            requestOptions = requestOptions.transform(new GlideRoundedCornersTransform(context, 4f, GlideRoundedCornersTransform.CornerType.ALL));
        }
        mRequestBuilder.apply(requestOptions);

    }

    @SuppressLint("CheckResult")
    @Override
    public <R> AbstractImageLoader listener(@NonNull final ImageLoaderRequestListener<R> listener) {

        mRequestBuilder.listener(new RequestListener<R>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<R> target,
                                        boolean isFirstResource) {
                String exceptionMsg = e == null ? "no msg" : e.getMessage();
                listener.onLoadFailed(exceptionMsg, isFirstResource);
                return false;
            }

            @Override
            public boolean onResourceReady(R resource, Object model, Target<R> target, DataSource dataSource,
                                           boolean isFirstResource) {
                listener.onResourceReady(resource, isFirstResource);
                return false;
            }
        });
        return this;
    }

    /**
     * 将加载的图片设置到imageView
     *
     * @param imageView ImageView
     */
    @Override
    public void into(@NonNull ImageView imageView) {
        mRequestBuilder.into(imageView);
    }

    /**
     * 将加载的图片设置到AbstractImageLoaderTarget
     *
     * @param view         用于适配加载图片的大小参数
     * @param loaderTarget AbstractImageLoaderTarget
     */
    @Override
    public <T> void into(@NonNull View view, @NonNull final AbstractImageLoaderTarget<T> loaderTarget) {

        mRequestBuilder.into(new CustomViewTarget<View, T>(view) {
            @Override
            public void onStart() {
                super.onStart();
                loaderTarget.onLoadStarted();
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                loaderTarget.onLoadFailed(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull T resource, @Nullable Transition<? super T> transition) {
                loaderTarget.onResourceReady(resource);
            }

            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {
                loaderTarget.onLoadCleared(placeholder);
            }
        });

    }

    /**
     * 减少引用计数
     *
     * @param context   Context
     * @param imageView imageView
     */
    @Override
    public void clear(@NonNull Context context, @NonNull ImageView imageView) {
        Glide.with(context).clear(imageView);
    }


}
