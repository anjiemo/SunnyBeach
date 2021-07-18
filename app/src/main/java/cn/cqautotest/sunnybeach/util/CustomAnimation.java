package cn.cqautotest.sunnybeach.util;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.Interpolator;

import com.chad.library.adapter.base.animation.BaseAnimation;

import org.jetbrains.annotations.NotNull;

/**
 * 自定义动画
 */
public class CustomAnimation implements BaseAnimation {
    @NotNull
    @Override
    public Animator[] animators(@NotNull View view) {
        Animator translationX =
                ObjectAnimator.ofFloat(view, "translationX", -view.getRootView().getWidth(), 0f);

        translationX.setDuration(800);
        translationX.setInterpolator(new MyInterpolator());

        return new Animator[]{translationX};
    }

    static class MyInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            float factor = 0.7f;
            return (float) (pow(2.0, -10.0 * input) * sin((input - factor / 4) * (2 * PI) / factor) + 1);
        }
    }
}