package cn.cqautotest.sunnybeach.http.annotation.baseurl

import cn.funkt.annotation.BaseUrl

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/23
 * desc   : 阿里云视频点播的 BaseUrl 注解
 */
@BaseUrl(value = "https://vod.cn-shanghai.aliyuncs.com")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AliyunVodBaseUrl
