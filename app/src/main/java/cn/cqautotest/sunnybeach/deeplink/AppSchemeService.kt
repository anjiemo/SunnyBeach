package cn.cqautotest.sunnybeach.deeplink

import android.content.Context
import android.content.Intent

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/03/05
 * desc   : DeepLink 接口定义
 */
interface AppSchemeService {

    fun navigation(context: Context, intent: Intent)
}