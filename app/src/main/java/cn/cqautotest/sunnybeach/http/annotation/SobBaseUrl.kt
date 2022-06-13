package cn.cqautotest.sunnybeach.http.annotation

import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SobBaseUrl(val value: String = SUNNY_BEACH_API_BASE_URL)
