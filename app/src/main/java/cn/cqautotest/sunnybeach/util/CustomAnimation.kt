package cn.cqautotest.sunnybeach.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Interpolator
import com.chad.library.adapter.base.animation.BaseAnimation
import kotlin.math.pow
import kotlin.math.sin

/**
 * 自定义动画
 */
class CustomAnimation : BaseAnimation {

    override fun animators(view: View): Array<Animator> {
        val translationX: Animator = ObjectAnimator.ofFloat(view, "translationX", -view.rootView.width.toFloat(), 0f)
        translationX.duration = 800
        translationX.interpolator = MyInterpolator()
        return arrayOf(translationX)
    }

    internal class MyInterpolator : Interpolator {
        override fun getInterpolation(input: Float): Float {
            val factor = 0.7f
            return (2.0.pow(-10.0 * input) * sin((input - factor / 4) * (2 * Math.PI) / factor) + 1).toFloat()
        }
    }
}