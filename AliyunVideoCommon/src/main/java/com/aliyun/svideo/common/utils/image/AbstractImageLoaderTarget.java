package com.aliyun.svideo.common.utils.image;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @param <R> The type of resource the target can display.
 * @author cross_ly
 * @date 2018/12/06
 */
public abstract class AbstractImageLoaderTarget<R> {


    public void onLoadStarted() {
    }

    public void onLoadFailed(@Nullable Drawable errorDrawable) {
    }

    public abstract void onResourceReady(@NonNull R resource);

    public void onLoadCleared(@Nullable Drawable placeholder) {
    }

}
