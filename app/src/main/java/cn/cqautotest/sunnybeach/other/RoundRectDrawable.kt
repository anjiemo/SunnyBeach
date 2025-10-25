package cn.cqautotest.sunnybeach.other

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.toRectF

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/18
 * desc   : Very simple drawable that draws a rounded rectangle background with arbitrary corners and also reports proper outline for Lollipop.
 * Simpler and uses less resources compared to GradientDrawable or ShapeDrawable.
 */
class RoundRectDrawable(@param:Px private val radius: Int, @param:ColorInt private val color: Int = Color.TRANSPARENT) : Drawable() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).also { it.color = color }

    override fun draw(canvas: Canvas) {
        val r = bounds.toRectF()
        canvas.drawRoundRect(r, radius.toFloat(), radius.toFloat(), mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}
