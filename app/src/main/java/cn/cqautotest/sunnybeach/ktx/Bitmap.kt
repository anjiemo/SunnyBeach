package cn.cqautotest.sunnybeach.ktx

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.graphics.set

fun Bitmap?.setTintColor(@ColorInt newColor: Int): Bitmap? {
    this ?: return null
    val newBitmap = copy(config, true)
    for (x in 0 until width) {
        for (y in 0 until height) {
            takeIf { getPixel(x, y) != Color.TRANSPARENT }?.let { newBitmap[x, y] = newColor }
        }
    }
    return newBitmap
}