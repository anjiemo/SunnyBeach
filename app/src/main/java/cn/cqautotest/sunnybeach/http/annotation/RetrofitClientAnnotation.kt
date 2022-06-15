package cn.cqautotest.sunnybeach.http.annotation

import cn.android52.network.annotation.BaseClient
import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/22
 * desc   : Use Sob RetrofitClient
 */
@BaseClient(value = SUNNY_BEACH_API_BASE_URL)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SobClient

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/22
 * desc   : Use CaiYun RetrofitClient
 */
@BaseClient(value = CAI_YUN_BASE_URL)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CaiYunClient