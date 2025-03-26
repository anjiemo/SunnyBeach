package cn.cqautotest.sunnybeach.deeplink

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.ktx.tryShowLoginDialog
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.PutFishActivity
import com.blankj.utilcode.util.ActivityUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/03/05
 * desc   : 发布动态 DeepLink 服务
 */
class PutFishServiceHandler : AppSchemeService {

    override fun navigation(context: Context, intent: Intent) {
        // 判断登录状态，如果未登录则去登录，已登录则跳转至发布动态界面
        if (UserManager.isLogin()) {
            context.startActivity<PutFishActivity>()
            return
        }
        val topActivity = ActivityUtils.getTopActivity()
        topActivity.tryShowLoginDialog(dismissPreviousDialog = true) {
            (topActivity as? LifecycleOwner)?.let {
                LiveBusUtils.busReceive<Unit>(it, LiveBusKeyConfig.BUS_LOGIN_SUCCESS, observer = object : Observer<Unit> {
                    override fun onChanged(value: Unit) {
                        // 及时移除监听，只使用一次
                        LiveBusUtils.busRemoveObserver(LiveBusKeyConfig.BUS_LOGIN_SUCCESS, this)
                        navigation(topActivity, intent)
                    }
                })
            }
        }
    }
}