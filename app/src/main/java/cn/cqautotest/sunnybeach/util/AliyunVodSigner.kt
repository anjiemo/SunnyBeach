package cn.cqautotest.sunnybeach.util

import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.EncryptUtils
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/23
 * desc   : 阿里云视频点播签名工具类
 */
object AliyunVodSigner {

    private const val ENCODING = "UTF-8"

    /**
     * 计算签名并返回完整的请求参数字典
     */
    fun sign(
        method: String,
        action: String,
        videoId: String,
        authInfo: String,
        accessKeyId: String,
        accessKeySecret: String,
        securityToken: String
    ): Map<String, String> {
        val params = mutableMapOf(
            "Action" to action,
            "VideoId" to videoId,
            "AuthInfo" to authInfo,
            "Format" to "JSON",
            "Version" to "2017-03-21",
            "AccessKeyId" to accessKeyId,
            "SignatureMethod" to "HMAC-SHA1",
            "SignatureNonce" to UUID.randomUUID().toString(),
            "SignatureVersion" to "1.0",
            "Timestamp" to getTimestamp(),
            "SecurityToken" to securityToken
        )

        val signature = calculateSignature(method, params, accessKeySecret)
        params["Signature"] = signature
        return params
    }

    private fun calculateSignature(method: String, params: Map<String, String>, accessKeySecret: String): String {
        // 参数排序并构造 CanonicalizedQueryString
        val sortedQueryString = params.entries
            .sortedBy { it.key }
            .joinToString("&") { "${percentEncode(it.key)}=${percentEncode(it.value)}" }

        // 构造 StringToSign
        val stringToSign = "$method&${percentEncode("/")}&${percentEncode(sortedQueryString)}"

        // 计算 HMAC-SHA1 并进行 Base64 编码
        val key = "$accessKeySecret&"
        val signData = EncryptUtils.encryptHmacSHA1(stringToSign.toByteArray(charset(ENCODING)), key.toByteArray(charset(ENCODING)))

        return EncodeUtils.base64Encode2String(signData)
    }

    private fun percentEncode(value: String): String {
        return URLEncoder.encode(value, ENCODING)
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~")
    }

    private fun getTimestamp(): String {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        df.timeZone = TimeZone.getTimeZone("GMT")
        return df.format(Date())
    }
}
