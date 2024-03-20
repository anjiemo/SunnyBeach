package cn.cqautotest.sunnybeach.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.annotation.DrawableRes

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2024/03/20
 * desc   : ImageSpan 兼容类，适配 api29 以下的[DynamicDrawableSpan.ALIGN_CENTER]属性
 */
object ImageSpanCompat {

    const val ALIGN_BOTTOM = DynamicDrawableSpan.ALIGN_BOTTOM
    const val ALIGN_BASELINE = DynamicDrawableSpan.ALIGN_BASELINE

    @SuppressLint("InlinedApi")
    const val ALIGN_CENTER = DynamicDrawableSpan.ALIGN_CENTER

    fun newImageSpan(context: Context, @DrawableRes resourceId: Int, verticalAlignment: Int): ImageSpan {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(context, resourceId, verticalAlignment)
        } else {
            CustomImageSpan(context, resourceId, verticalAlignment)
        }
    }

    fun newImageSpan(context: Context, bitmap: Bitmap, verticalAlignment: Int): ImageSpan {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(context, bitmap, verticalAlignment)
        } else {
            CustomImageSpan(context, bitmap, verticalAlignment)
        }
    }

    fun newImageSpan(drawable: Drawable, source: String, verticalAlignment: Int): ImageSpan {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(drawable, source, verticalAlignment)
        } else {
            CustomImageSpan(drawable, source, verticalAlignment)
        }
    }

    fun newImageSpan(context: Context, uri: Uri, verticalAlignment: Int): ImageSpan {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ImageSpan(context, uri, verticalAlignment)
        } else {
            CustomImageSpan(context, uri, verticalAlignment)
        }
    }
}