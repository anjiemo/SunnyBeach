package cn.cqautotest.sunnybeach.deeplink

import android.content.Context
import android.content.Intent
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/21
 * desc   : 扫码 DeepLink 服务
 */
class ScanCodeServiceHandler : AppSchemeService {

    override fun navigation(context: Context, intent: Intent) {
        LiveBusUtils.busSend(LiveBusKeyConfig.BUS_HOME_START_SCAN, Unit)
    }
}
