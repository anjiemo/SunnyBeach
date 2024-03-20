package cn.cqautotest.sunnybeach.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.core.graphics.withTranslation
import java.lang.ref.WeakReference

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2024/03/20
 * desc   : 自定义 ImageSpan 类，适配 api29 以下的[DynamicDrawableSpan.ALIGN_CENTER]属性
 */
class CustomImageSpan : ImageSpan {

    private var mDrawableRef: WeakReference<Drawable>? = null

    constructor(context: Context, bitmap: Bitmap) : super(context, bitmap)
    constructor(context: Context, bitmap: Bitmap, verticalAlignment: Int) : super(context, bitmap, verticalAlignment)
    constructor(drawable: Drawable) : super(drawable)
    constructor(drawable: Drawable, verticalAlignment: Int) : super(drawable, verticalAlignment)
    constructor(drawable: Drawable, source: String) : super(drawable, source)
    constructor(drawable: Drawable, source: String, verticalAlignment: Int) : super(drawable, source, verticalAlignment)
    constructor(context: Context, uri: Uri) : super(context, uri)
    constructor(context: Context, uri: Uri, verticalAlignment: Int) : super(context, uri, verticalAlignment)
    constructor(context: Context, resourceId: Int) : super(context, resourceId)
    constructor(context: Context, resourceId: Int, verticalAlignment: Int) : super(context, resourceId, verticalAlignment)

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && verticalAlignment == DynamicDrawableSpan.ALIGN_CENTER) {
            cachedDrawable?.let { b ->
                var transY = bottom - b.bounds.bottom
                transY -= top + (bottom - top) / 2 - b.getBounds().height() / 2
                canvas.withTranslation(x, transY.toFloat()) {
                    b.draw(this)
                }
            }
        } else {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint)
        }
    }

    private val cachedDrawable: Drawable?
        get() {
            return mDrawableRef?.get() ?: drawable.also {
                mDrawableRef = WeakReference(it)
            }
        }
}