package cn.cqautotest.sunnybeach.http.annotation

import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CaiYunBaseUrl(val value: String = CAI_YUN_BASE_URL)
