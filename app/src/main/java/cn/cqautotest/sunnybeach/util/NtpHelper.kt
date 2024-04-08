package cn.cqautotest.sunnybeach.util

import android.os.SystemClock
import com.blankj.utilcode.util.TimeUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.coroutines.resume

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2024/02/11
 * desc   : NTP时间帮助类
 */
object NtpHelper {

    const val INVALID_TIME = -1L
    private const val NTP_PORT = 123
    private const val NTP_PACKET_SIZE = 48
    private const val NTP_MODE_CLIENT = 3
    private const val NTP_VERSION = 4
    private const val NTP_UNIX_EPOCH_DIFFERENCE: Long = 2208988800000
    private const val DEFAULT_TIME_OUT = 5000
    private val ntpServiceList = arrayOf(
        "ntp1.aliyun.com",
        "ntp2.aliyun.com",
        "ntp3.aliyun.com",
        "ntp4.aliyun.com",
        "ntp5.aliyun.com",
        "ntp6.aliyun.com",
        "ntp7.aliyun.com",
        "time1.aliyun.com",
        "time2.aliyun.com",
        "time3.aliyun.com",
        "time4.aliyun.com",
        "time5.aliyun.com",
        "time6.aliyun.com",
        "time7.aliyun.com",
        "ntp.ntsc.ac.cn",
        "cn.ntp.org.cn",
        "s1a.time.edu.cn",
        "ntp1.nim.ac.cn",
        "ntp.tuna.tsinghua.edu.cn",
        "ntp.sjtu.edu.cn",
        "ntp.neusoft.edu.cn",
        "ntp2.nim.ac.cn",
        "time.ustc.edu.cn",
        "0.cn.pool.ntp.org",
        "1.cn.pool.ntp.org",
        "2.cn.pool.ntp.org",
        "3.cn.pool.ntp.org",
        "time.windows.com",
        "time.apple.com",
        "time.asia.apple.com",
    )
    private var originElapsedRealtime = SystemClock.elapsedRealtime()
    private var mNtpTime = INVALID_TIME

    /**
     * Make sure that the [fetchFastestNTPResponseTime] function was successfully called.
     * see：[fetchFastestNTPResponseTime]
     */
    fun getRealTime(): Long {
        return mNtpTime.takeUnless { it == INVALID_TIME }?.let {
            mNtpTime + (SystemClock.elapsedRealtime() - originElapsedRealtime)
        } ?: INVALID_TIME
    }

    /**
     * By default, the current time is estimated using the last obtained NTP time, and if it is not,
     * the current NTP time is immediately obtained and cached.
     * see：[getRealTime], [getNTPTime]
     */
    suspend fun fetchFastestNTPResponseTime(timeout: Int = DEFAULT_TIME_OUT, forceRefresh: Boolean = false): Long {
        takeUnless { forceRefresh }?.let {
            getRealTime().takeUnless { it == INVALID_TIME }?.let { return it }
        }
        return withContext(Dispatchers.IO) {
            val deferredList = ntpServiceList.map { host ->
                async { HostAndTime(host, getNTPTime(host, timeout)) }
            }
            var fastestNTPTime: HostAndTime? = null
            select {
                // prioritizes the fastest one
                deferredList.forEach { deferred ->
                    deferred.onAwait { hostAndTime ->
                        fastestNTPTime = hostAndTime
                        // cancel other requests
                        deferredList.forEach { it.cancel() }
                    }
                }
            }
            // 打印 NTP 时间
            fastestNTPTime?.let { hostAndTime ->
                Timber.d("getFastestNTPTime：===> host is ${hostAndTime.host}, at ${TimeUtils.millis2String(hostAndTime.time)}")
            }
            fastestNTPTime?.time?.also { mNtpTime = it } ?: INVALID_TIME
        }
    }

    /**
     * Get the current NTP time from the given host.
     */
    suspend fun getNTPTime(host: String, timeout: Int = DEFAULT_TIME_OUT): Long = suspendCancellableCoroutine { continuation ->
        runCatching {
            // 为 NTP 请求创建缓冲区
            val ntpRequest = ByteArray(NTP_PACKET_SIZE)
            // 设置 NTP 模式为客户端，版本为4
            ntpRequest[0] = (NTP_MODE_CLIENT or (NTP_VERSION shl 3)).toByte()

            val socket = DatagramSocket()
            continuation.invokeOnCancellation {
                socket.close()
            }
            socket.soTimeout = timeout
            val requestPacket = DatagramPacket(ntpRequest, ntpRequest.size, InetAddress.getByName(host), NTP_PORT)
            val t1 = System.currentTimeMillis()
            // 发送 NTP 请求到服务器
            socket.send(requestPacket)
            // 接收 NTP 响应
            val ntpResponse = ByteArray(NTP_PACKET_SIZE)
            val responsePacket = DatagramPacket(ntpResponse, ntpResponse.size)
            socket.receive(responsePacket)
            socket.use {
                val t4 = System.currentTimeMillis()
                // 将接收到的NTP响应数据字段转换为时间（从1900年起的毫秒值）
                val t2 = ((ntpResponse[32].toLong() and 0xFF shl 24) or
                        (ntpResponse[33].toLong() and 0xFF shl 16) or
                        (ntpResponse[34].toLong() and 0xFF shl 8) or
                        (ntpResponse[35].toLong() and 0xFF)) * 1000L - NTP_UNIX_EPOCH_DIFFERENCE
                val t3 = ((ntpResponse[40].toLong() and 0xFF shl 24) or
                        (ntpResponse[41].toLong() and 0xFF shl 16) or
                        (ntpResponse[42].toLong() and 0xFF shl 8) or
                        (ntpResponse[43].toLong() and 0xFF)) * 1000L - NTP_UNIX_EPOCH_DIFFERENCE
                val delay = (t4 - t1) - (t3 - t2)
                val offset = ((t2 - t1) + (t3 - t4)) / 2
                Timber.d("getNTPTime：===> Round trip delay: $delay ms")
                originElapsedRealtime = SystemClock.elapsedRealtime()
                System.currentTimeMillis() + offset
            }
        }.onSuccess { ntpTime ->
            continuation.resume(ntpTime)
        }.onFailure {
            throw CancellationException(it.message)
        }
    }
}

data class HostAndTime(val host: String, val time: Long)