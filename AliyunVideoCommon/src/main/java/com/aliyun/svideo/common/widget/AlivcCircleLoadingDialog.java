package com.aliyun.svideo.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.aliyun.svideo.common.R;

/**
 * @author cross_ly
 * @date 2018/09/20 <p>描述:自定义loading dialog
 */
public class AlivcCircleLoadingDialog extends Dialog {
    private final int mHeight;
    private Context context;
    private ImageView mImageView;

    public AlivcCircleLoadingDialog(Context context, int height) {
        super(context, R.style.CustomDialogStyle);
        this.context = context;
        this.mHeight = height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alivc_common_dialog_circle_progress, null, false);
        mImageView = view.findViewById(R.id.iv_dialog_progress);
        mImageView.setImageResource(R.mipmap.alivc_common_icon_circle_progress);
        setAnimation();
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = d.widthPixels; // 高度设置为屏幕的高
        if (mHeight == 0) {
            lp.height = d.heightPixels; // 高度设置为屏幕的宽
        } else {
            lp.height = mHeight;
        }
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        dialogWindow.setAttributes(lp);
        setCancelable(false);
    }

    private void setAnimation() {

        /**
         *
         * @param fromDegrees 开始的度数
         * @param toDegrees 结束的度数
         * Type：Animation.ABSOLUTE：绝对，如果设置这种类型，后面pivotXValue取值就必须是像素点；比如：控件X方向上的中心点，pivotXValue的取值mIvImg.getWidth() / 2f
         *            Animation.RELATIVE_TO_SELF：相对于控件自己，设置这种类型，后面pivotXValue取值就会去拿这个取值是乘上控件本身的宽度；比如：控件X方向上的中心点，pivotXValue的取值0.5f
         *            Animation.RELATIVE_TO_PARENT：相对于它父容器（这个父容器是指包括这个这个做动画控件的外一层控件）， 原理同上，
         * @param pivotXValue  配合pivotXType使用，原理在上面
         * @param pivotYType 原理同上
         * @param pivotYValue 原理同上
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        //设置动画持续时长
        rotateAnimation.setDuration(800);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        //设置动画的重复模式：反转REVERSE和重新开始RESTART
        rotateAnimation.setRepeatMode(ScaleAnimation.RESTART);
        //设置动画播放次数
        rotateAnimation.setRepeatCount(ScaleAnimation.INFINITE);
        //开始动画
        if (mImageView.getAnimation() == null) {
            mImageView.startAnimation(rotateAnimation);
        }

    }

    @Override
    public void show() {
        super.show();
        if (mImageView != null) {
            setAnimation();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mImageView != null) {
            mImageView.clearAnimation();
        }
    }
}
