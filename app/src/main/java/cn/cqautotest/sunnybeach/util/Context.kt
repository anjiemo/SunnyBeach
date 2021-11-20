package cn.cqautotest.sunnybeach.util

import android.content.Context
import android.content.Intent
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity

fun Context.startActivity(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

inline fun <reified T> Context.startActivity() {
    startActivity(T::class.java)
}

/**
 * 检查用户登录状态，建议在请求数据前调用
 */
fun Context.checkUserLoginState() {
    val userBasicInfo = UserManager.loadUserBasicInfo()
    takeIf { userBasicInfo == null }?.let {
        LoginActivity.start(this, "", "")
    }
}