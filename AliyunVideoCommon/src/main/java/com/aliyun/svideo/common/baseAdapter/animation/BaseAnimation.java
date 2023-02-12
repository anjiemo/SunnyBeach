package com.aliyun.svideo.common.baseAdapter.animation;

import android.animation.Animator;
import android.view.View;

/**
 * 基recyclervView的item础动画接口
 */
public interface BaseAnimation {
    Animator[] getAnimators(View view);
}