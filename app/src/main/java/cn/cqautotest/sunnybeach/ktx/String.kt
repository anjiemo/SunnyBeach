package cn.cqautotest.sunnybeach.ktx

import android.graphics.Bitmap
import android.graphics.Color
import com.blankj.utilcode.util.EncryptUtils
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import java.util.regex.Matcher
import java.util.regex.Pattern

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

fun String.unicodeToString(): String {
    var str = this
    val pattern: Pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))")
    val matcher: Matcher = pattern.matcher(str)
    var ch: Char
    runCatching {
        while (matcher.find()) {
            //group 6728
            val group: String? = matcher.group(2)
            group ?: return this
            //ch:'木' 26408
            ch = group.toInt(16).toChar()
            //group1 \u6728
            val group1: String? = matcher.group(1)
            group1 ?: return this
            str = str.replace(group1, ch.toString() + "")
        }
    }
    return str
}