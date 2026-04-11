package cn.cqautotest.sunnybeach.ktx

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.RegexUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.Hashtable

/**
 * 将字符串转换成二维码图片，使用 ZXing 优化并对齐项目原有 API
 */
fun String.toQrCodeBitmapOrNull(
    size: Int = 400,
    logo: Bitmap? = null,
    qrColor: Int = Color.BLACK,
    bgColor: Int = Color.WHITE,
    margin: Int = 1
): Bitmap? {
    if (isEmpty()) return null
    return try {
        val hints = Hashtable<EncodeHintType, Any>().apply {
            put(EncodeHintType.CHARACTER_SET, "utf-8")
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            put(EncodeHintType.MARGIN, margin)
        }
        val bitMatrix = MultiFormatWriter().encode(this, BarcodeFormat.QR_CODE, size, size, hints)
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                pixels[y * size + x] = if (bitMatrix[x, y]) qrColor else bgColor
            }
        }
        val result = createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
        logo?.let { addLogo(result, it) } ?: result
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 在二维码中间添加 Logo，使用官方 KTX 简化绘制逻辑
 */
private fun addLogo(src: Bitmap, logo: Bitmap): Bitmap {
    val srcWidth = src.width
    val srcHeight = src.height
    val logoWidth = logo.width
    val logoHeight = logo.height
    val scaleFactor = srcWidth * 1.0f / 5 / logoWidth
    return src.copy(Bitmap.Config.ARGB_8888, true).applyCanvas {
        save()
        scale(scaleFactor, scaleFactor, srcWidth / 2f, srcHeight / 2f)
        drawBitmap(logo, (srcWidth - logoWidth) / 2f, (srcHeight - logoHeight) / 2f, null)
        restore()
    }
}

fun String?.ifNullOrEmpty(defaultValue: () -> String): String = if (isNullOrEmpty()) defaultValue() else this!!

fun String?.ifNullOrBlank(defaultValue: () -> String): String = if (isNullOrBlank()) defaultValue() else this!!

fun String.notContains(other: CharSequence, ignoreCase: Boolean = false) = !contains(other, ignoreCase)

fun String.notContains(char: Char, ignoreCase: Boolean = false) = !contains(char, ignoreCase)

fun String.notContains(regex: Regex) = !contains(regex)

val String.lowercaseMd5: String
    get() = md5.lowercase()

val String.md5: String
    get() = EncryptUtils.encryptMD5ToString(this)

fun String.unicodeToString(): String = runCatching {
    Regex("\\\\u(\\p{XDigit}{4})").replace(this) { matchResult ->
        matchResult.groupValues[1].toInt(16).toChar().toString()
    }
}.getOrDefault(this)

/**
 * 如果当前字符串不是邮箱，则原样返回。
 * 否则，返回掩码处理后的邮箱地址。
 */
fun String.maskEmail(): String {
    // 如果不是邮箱，则返回当前字符串
    takeUnless { RegexUtils.isEmail(this) }?.let { return this }
    val regex = Regex("(^.{2,3})(.*)(@.*)")
    return replace(regex) { "${it.groupValues[1]}***${it.groupValues[3]}" }
}