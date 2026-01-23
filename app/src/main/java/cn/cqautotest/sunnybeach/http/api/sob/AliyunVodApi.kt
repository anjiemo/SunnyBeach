package cn.cqautotest.sunnybeach.http.api.sob

import cn.cqautotest.sunnybeach.http.annotation.baseurl.AliyunVodBaseUrl
import cn.cqautotest.sunnybeach.model.aliyun.AliyunVodResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/23
 * desc   : 阿里云视频点播 API
 */
@AliyunVodBaseUrl
interface AliyunVodApi {

    /**
     * 获取视频播放地址
     * 注意：此请求需要手动计算签名并补全公共请求参数
     */
    @GET("/")
    suspend fun getPlayInfo(@QueryMap params: Map<String, String>): AliyunVodResponse
}
