package cn.cqautotest.sunnybeach.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity

fun Context.startActivity(clazz: Class<*>) {
    val intent = Intent(this, clazz).apply {
        if (this !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    startActivity(intent)
}

inline fun <reified T> Context.startActivity() {
    startActivity(T::class.java)
}

/**
 * 检查用户登录状态，建议在需要登录状态的界面跳转前调用
 */
fun Context.checkUserLoginState(block: (userBasicInfo: UserBasicInfo) -> Unit) {
    when (val userBasicInfo = UserManager.loadUserBasicInfo()) {
        // 账号未登录
        null -> LoginActivity.start(this, "", "")
        // 账号已登录，执行 lambda 中的操作
        else -> block.invoke(userBasicInfo)
    }
}