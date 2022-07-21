package cn.cqautotest.sunnybeach.ktx

import android.content.Context
import androidx.core.app.ComponentActivity
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog

/**
 * 解析当前用户的 Token
 */
fun ComponentActivity.checkToken(block: (userBasicInfo: Result<UserBasicInfo>) -> Unit) {
    when (val result = UserManager.loadUserBasicInfo()) {
        null -> block.invoke(Result.failure(NotLoginException()))
        else -> block.invoke(Result.success(result))
    }
}

/**
 * 检查是否用户登录
 * 如果没有登录则跳转至登录界面，否则执行 block Lambda 中的代码
 */
private var isShowing = false
fun ComponentActivity.takeIfLogin(block: (userBasicInfo: UserBasicInfo) -> Unit) {
    checkToken {
        when (val userBasicInfo = it.getOrNull()) {
            null -> {
                if (isShowing) {
                    return@checkToken
                }
                showLoginDialog()
            }
            else -> block.invoke(userBasicInfo)
        }
    }
}

fun Context.showLoginDialog() {
    MessageDialog.Builder(this)
        .setTitle("系统消息")
        .setMessage("账号未登录，是否登录？")
        .setConfirm("现在登录")
        .setCancel("暂不登录")
        .addOnShowListener {
            isShowing = true
        }
        .addOnDismissListener {
            isShowing = false
        }
        .setListener {
            LoginActivity.start(this, UserManager.getCurrLoginAccount(), UserManager.getCurrLoginAccountPassword())
        }.show()
}