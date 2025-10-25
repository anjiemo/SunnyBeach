package cn.cqautotest.sunnybeach.other

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.view.ViewDebug.ExportedProperty
import androidx.annotation.ColorInt
import androidx.appcompat.widget.LinearLayoutCompat

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/18
 * desc   : 多色 Drawable，可设置填充方向 LinearLayoutCompat.HORIZONTAL or LinearLayoutCompat.VERTICAL，默认为 LinearLayoutCompat.VERTICAL 。
 */
class MultiColorDrawable(@field:ColorInt private val colorArr: Array<Int>) : Drawable() {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    @ExportedProperty(category = "measurement")
    private var mOrientation = LinearLayoutCompat.VERTICAL

    /**
     * Should the layout be a column or a row.
     * @param orientation Pass [.HORIZONTAL] or [.VERTICAL]. Default
     * value is [.VERTICAL].
     */
    fun setOrientation(@LinearLayoutCompat.OrientationMode orientation: Int) {
        if (mOrientation != orientation) {
            mOrientation = orientation
            invalidateSelf()
        }
    }

    /**
     * Returns the current orientation.
     *
     * @return either [.HORIZONTAL] or [.VERTICAL]
     */
    @LinearLayoutCompat.OrientationMode
    fun getOrientation(): Int {
        return mOrientation
    }

    override fun draw(canvas: Canvas) {
        val r = bounds
        val width = r.width()
        val height = r.height()
        val count = colorArr.count()
        if (getOrientation() == LinearLayoutCompat.VERTICAL) {
            drawVerticalStyle(canvas, width, height, count)
        } else {
            drawHorizontalStyle(canvas, width, height, count)
        }
    }

    private fun drawHorizontalStyle(canvas: Canvas, width: Int, height: Int, count: Int) {
        colorArr.forEachIndexed { index, color ->
            mPaint.color = color
            canvas.drawRect(width * ((index * 1f) / count), 0f, width * ((index + 1f) / count), height * 1f, mPaint)
        }
    }

    private fun drawVerticalStyle(canvas: Canvas, width: Int, height: Int, count: Int) {
        colorArr.forEachIndexed { index, color ->
            mPaint.color = color
            canvas.drawRect(0f, height * ((index * 1f) / count), width * 1f, height * ((index + 1f) / count), mPaint)
        }
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
