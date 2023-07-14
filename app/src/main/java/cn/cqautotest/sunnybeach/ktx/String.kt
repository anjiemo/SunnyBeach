package cn.cqautotest.sunnybeach.ktx

import android.graphics.Bitmap
import android.graphics.Color
import com.blankj.utilcode.util.EncryptUtils
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan

fun String?.toQrCodeBitmapOrNull(
    size: Int = 400,
    bgColor: Int = Color.WHITE,
    qrColor: Int = Color.BLACK,
    margin: Int = 2,
    qrLogoBitmap: Bitmap? = null
): Bitmap? {
    val type = HmsScan.QRCODE_SCAN_TYPE
    val options = HmsBuildBitmapOption.Creator()
        .setBitmapBackgroundColor(bgColor)
        .setBitmapColor(qrColor)
        .setBitmapMargin(margin)
        .setQRLogoBitmap(qrLogoBitmap)
        .create()
    return try {
        // 如果未设置HmsBuildBitmapOption对象，生成二维码参数options置null。
        ScanUtil.buildBitmap(this, type, size, size, options)
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}

fun String?.ifNullOrEmpty(defaultValue: () -> String) = if (isNullOrEmpty()) defaultValue() else this

fun String?.ifNullOrBlank(defaultValue: () -> String) = if (isNullOrBlank()) defaultValue() else this

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