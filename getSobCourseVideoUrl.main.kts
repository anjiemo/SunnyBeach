@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("com.squareup.okhttp3:okhttp:4.12.0")
@file:DependsOn("com.google.code.gson:gson:2.13.2")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * 阳光沙滩视频播放链接获取脚本
 * 运行方式：在 IntelliJ IDEA 或 AndroidStudio 中右键点击并选择 "Run 'getSobCourseVideoUrl.main.kts'"
 * 
 * 注意：如果 IDE 提示 "Unresolved reference"，请点击编辑器顶部的 "Load Script Dependencies" 或 "Sync" 按钮。
 */

// --- 配置区 ---
private val SOB_TOKEN_VALUE = "p_xxx" // 请替换为实际的 sob_token，可登录阳光沙滩官网后，通过F12控制台获取
private val CIID = "1478002661176410114" // 课程 ID
// --------------

private val client = OkHttpClient()
private val gson = Gson()

/**
 * 阿里云请求签名辅助对象
 */
object AliyunRequestSigner {

    @OptIn(ExperimentalEncodingApi::class)
    fun calculateSignature(method: String, params: Map<String, String>, accessKeySecret: String): String {
        val sortedQueryString = params.entries
            .sortedBy { it.key }
            .joinToString("&") { "${percentEncode(it.key)}=${percentEncode(it.value)}" }

        val stringToSign = "$method&${percentEncode("/")}&${percentEncode(sortedQueryString)}"

        val key = "$accessKeySecret&".toByteArray()
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(key, "HmacSHA1"))
        val signData = mac.doFinal(stringToSign.toByteArray())

        return Base64.encode(signData)
    }

    fun percentEncode(value: String): String = URLEncoder.encode(value, "UTF-8")
        .replace("+", "%20")
        .replace("*", "%2A")
        .replace("%7E", "~")

    fun getISO8601Timestamp(): String {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        df.timeZone = TimeZone.getTimeZone("GMT")
        return df.format(Date())
    }
}

/**
 * 从阳光沙滩获取视频认证信息 (videoId, playAuth)
 */
private suspend fun fetchCertification(ciid: String): Pair<String, String> = withContext(Dispatchers.IO) {
    println("[1/2] 正在获取播放凭证...")
    val url = "https://api.sunofbeach.net/ct/video/certification/$ciid"
    val request = Request.Builder()
        .url(url)
        .addHeader("cookie", "sob_token=$SOB_TOKEN_VALUE")
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw Exception("凭证接口请求失败 (${response.code})")

        val body = response.body?.string() ?: ""
        val json = gson.fromJson(body, JsonObject::class.java)
        if (!json.get("success").asBoolean) throw Exception("凭证接口返回异常: ${json.get("message").asString}")

        val data = json.getAsJsonObject("data")
        data.get("videoId").asString to data.get("playAuth").asString
    }
}

/**
 * 解析 Aliyun PlayAuth 并获取实际播放信息
 */
@OptIn(ExperimentalEncodingApi::class)
private suspend fun fetchAliyunPlayInfo(videoId: String, playAuth: String): List<JsonObject> = withContext(Dispatchers.IO) {
    println("[2/2] 正在解析播放凭证并请求阿里云接口...")

    val decodedAuth = String(Base64.Default.decode(playAuth))
    val authJson = gson.fromJson(decodedAuth, JsonObject::class.java)

    val accessKeyId = authJson.get("AccessKeyId").asString
    val accessKeySecret = authJson.get("AccessKeySecret").asString
    val securityToken = authJson.get("SecurityToken").asString
    val authInfo = authJson.get("AuthInfo").asString
    val region = "cn-shanghai"

    val params = mutableMapOf(
        "Action" to "GetPlayInfo",
        "VideoId" to videoId,
        "AuthInfo" to authInfo,
        "Format" to "JSON",
        "Version" to "2017-03-21",
        "AccessKeyId" to accessKeyId,
        "SignatureMethod" to "HMAC-SHA1",
        "SignatureNonce" to UUID.randomUUID().toString(),
        "SignatureVersion" to "1.0",
        "Timestamp" to AliyunRequestSigner.getISO8601Timestamp(),
        "SecurityToken" to securityToken
    )

    val signature = AliyunRequestSigner.calculateSignature("GET", params, accessKeySecret)
    params["Signature"] = signature

    val queryString = params.entries
        .sortedBy { it.key }
        .joinToString("&") { "${AliyunRequestSigner.percentEncode(it.key)}=${AliyunRequestSigner.percentEncode(it.value)}" }

    val url = "https://vod.$region.aliyuncs.com/?$queryString"

    val request = Request.Builder().url(url).build()
    client.newCall(request).execute().use { response ->
        val body = response.body?.string() ?: ""
        if (!response.isSuccessful) throw Exception("获取播放链接失败: $body")

        val playInfoJson = gson.fromJson(body, JsonObject::class.java)
        playInfoJson.getAsJsonObject("PlayInfoList")
            .getAsJsonArray("PlayInfo")
            .map { it.asJsonObject }
    }
}

// 脚本入口
runBlocking {
    println("--------------------------------------------------")
    println(">>> 阳光沙滩视频播放信息获取工具")
    println("--------------------------------------------------")

    runCatching {
        val (videoId, playAuth) = fetchCertification(CIID)
        println("成功获取 VideoID: $videoId")

        val playInfoList = fetchAliyunPlayInfo(videoId, playAuth)

        println("\n>>> 已获取到以下播放链接:")
        playInfoList.forEach { info ->
            val obj = info.asJsonObject
            val definition = obj.get("Definition").asString
            val playUrl = obj.get("PlayURL").asString
            println("  [$definition] -> $playUrl")
        }
    }.onFailure {
        println("\n[错误] 任务执行失败: ${it.message}")
    }

    println("\n--------------------------------------------------")
}
