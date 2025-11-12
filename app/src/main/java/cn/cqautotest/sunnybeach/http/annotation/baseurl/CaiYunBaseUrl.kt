package cn.cqautotest.sunnybeach.http.annotation.baseurl

import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import cn.funkt.annotation.BaseUrl

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/14
 * desc   : 彩云天气的 BaseUrl 注解
 */
@BaseUrl(value = CAI_YUN_BASE_URL)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CaiYunBaseUrl
