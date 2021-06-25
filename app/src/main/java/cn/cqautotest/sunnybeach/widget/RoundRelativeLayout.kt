package cn.cqautotest.sunnybeach.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.RelativeLayout
import cn.cqautotest.sunnybeach.R

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/06/18
 *    desc   : 圆角剪裁布局控件
 */
class RoundRelativeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mPath: Path = Path()
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mRectF: RectF = RectF()
    private var mRadius = 0f
    private var isClipBackground = false

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundRelativeLayout)
        mRadius = ta.getDimension(R.styleable.RoundRelativeLayout_rlRadius, 0f)
        isClipBackground = ta.getBoolean(R.styleable.RoundRelativeLayout_rlClipBackground, true)
        ta.recycle()
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    fun setRadius(radius: Float) {
        mRadius = radius
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRectF[0f, 0f, w.toFloat()] = h.toFloat()
    }

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas) {
        if (Build.VERSION.SDK_INT >= 28) {
            draw28(canvas)
        } else {
            draw27(canvas)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (Build.VERSION.SDK_INT >= 28) {
            dispatchDraw28(canvas)
        } else {
            dispatchDraw27(canvas)
        }
    }

    private fun draw27(canvas: Canvas) {
        if (isClipBackground) {
            canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG)
            super.draw(canvas)
            canvas.drawPath(genPath(), mPaint)
            canvas.restore()
        } else {
            super.draw(canvas)
        }
    }

    private fun draw28(canvas: Canvas) {
        if (isClipBackground) {
            canvas.save()
            canvas.clipPath(genPath())
            super.draw(canvas)
            canvas.restore()
        } else {
            super.draw(canvas)
        }
    }

    private fun dispatchDraw27(canvas: Canvas) {
        canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG)
        super.dispatchDraw(canvas)
        canvas.drawPath(genPath(), mPaint)
        canvas.restore()
    }

    private fun dispatchDraw28(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(genPath())
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    private fun genPath(): Path {
        mPath.reset()
        mPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW)
        return mPath
    }
}