package cn.cqautotest.sunnybeach.ktx

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.set

fun Bitmap?.setTintColor(@ColorInt newColor: Int): Bitmap? {
    val originBitmap = this ?: return null
    val newBitmap = originBitmap.copy(originBitmap.config, true)
    val width = originBitmap.width
    val height = originBitmap.height
    for (x in 0 until width) {
        for (y in 0 until height) {
            val originColor = originBitmap.getPixel(x, y)
            takeIf { originColor != Color.TRANSPARENT }?.let { newBitmap[x, y] = newColor }
        }
    }
    return newBitmap
}