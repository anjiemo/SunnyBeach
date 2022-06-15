package cn.cqautotest.sunnybeach.http.annotation

import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/14
 * desc   : 彩云天气的 BaseUrl 注解
 */
@cn.android52.network.annotation.BaseUrl(value = CAI_YUN_BASE_URL)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CaiYunBaseUrl
