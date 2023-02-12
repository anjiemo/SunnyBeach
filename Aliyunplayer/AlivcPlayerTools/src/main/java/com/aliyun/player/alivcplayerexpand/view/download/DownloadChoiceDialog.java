package com.aliyun.player.alivcplayerexpand.view.download;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;

import com.aliyun.player.alivcplayerexpand.R;
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode;
import com.aliyun.player.aliyunplayerbase.util.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * @author Mulberry
 * create on 2018/4/17.
 */

public class DownloadChoiceDialog extends Dialog {
    private final static int ANIMATION_DURATION = 200;
    private View mContentView;
    private AliyunScreenMode screenMode;
    private WeakReference<Context> activityWeakReference;
    private boolean mIsAnimating = false;

    public DownloadChoiceDialog(Context context, AliyunScreenMode aliyunScreenMode) {
        super(context, R.style.addDownloadDialog);
        this.screenMode = aliyunScreenMode;
        activityWeakReference = new WeakReference<Context>(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        //// 在底部，宽度撑满
        //WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        //params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        //
        //int screenWidth = ScreenUtils.getWidth(activityWeakReference.get());
        //int screenHeight = ScreenUtils.getHeight(activityWeakReference.get());
        //params.width = screenWidth < screenHeight ? screenWidth : screenHeight;
        //getWindow().setAttributes(params);
        //setCanceledOnTouchOutside(true);
        setLayoutBySreenMode(screenMode);
    }

    @Override
    public void setContentView(@NonNull View view) {
        mContentView = view;
        super.setContentView(view);
    }

    public void setLayoutBySreenMode(AliyunScreenMode aliyunScreenMode) {
        if (aliyunScreenMode == AliyunScreenMode.Small) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM | Gravity.CENTER;

            int screenWidth = ScreenUtils.getWidth(getContext());
            int screenHeight = ScreenUtils.getHeight(getContext());
            params.width = screenWidth < screenHeight ? screenWidth : screenHeight;
            getWindow().setAttributes(params);
            setCanceledOnTouchOutside(true);
        } else {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.RIGHT;

            int screenWidth = ScreenUtils.getWidth(getContext());
            int screenHeight = ScreenUtils.getHeight(getContext());
            params.width = screenWidth < screenHeight ? screenWidth : screenHeight;
            getWindow().setAttributes(params);
            setCanceledOnTouchOutside(true);
        }
    }

    /**
     * ChoiceItemBottomDialog从下往上升起的动画动画
     */
    private void animateUp() {
        if (mContentView != null) {
            return;
        }
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setInterpolator(new DecelerateInterpolator());
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setFillAfter(true);
        mContentView.startAnimation(animationSet);
    }

    /**
     * ChoiceItemBottomDialog从下往上升起的动画动画
     */
    private void animateDown() {
        if (mContentView == null) {
            return;
        }
        TranslateAnimation translate = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
        );
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translate);
        set.addAnimation(alpha);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(ANIMATION_DURATION);
        set.setFillAfter(true);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimating = false;
                mContentView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DownloadChoiceDialog.super.dismiss();
                        } catch (Exception e) {
                            Log.w("Test", "dismiss error\n" + Log.getStackTraceString(e));
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mContentView.startAnimation(set);
    }

    @Override
    public void show() {
        super.show();
        animateUp();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animateDown();
    }
}
