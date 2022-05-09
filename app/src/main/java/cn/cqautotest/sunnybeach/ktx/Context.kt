package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity

fun Context.asInflater() = LayoutInflater.from(this)

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val context = this
    Intent(this, T::class.java).apply(block).run {
        if (context !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(this)
    }
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