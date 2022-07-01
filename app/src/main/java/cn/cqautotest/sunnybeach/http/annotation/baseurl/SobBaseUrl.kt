package cn.cqautotest.sunnybeach.http.annotation.baseurl

import cn.android52.network.annotation.BaseUrl
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/14
 * desc   : 阳光沙滩的 BaseUrl 注解
 */
@BaseUrl(value = SUNNY_BEACH_API_BASE_URL)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SobBaseUrl
