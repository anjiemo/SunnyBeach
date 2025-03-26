package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.content.Context
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 解析当前用户的 Token
 */
suspend fun checkToken(block: ((userBasicInfo: Result<UserBasicInfo>) -> Unit)? = null) {
    when (val result = Repository.checkToken()) {
        null -> block?.invoke(Result.failure(NotLoginException()))
        else -> block?.invoke(Result.success(result))
    }
}

/**
 * We use an atomic boolean to mark whether the login dialog is currently displayed.
 */
private val isShowing = AtomicBoolean(false)

/**
 * 检查是否用户登录
 * 如果没有登录则跳转至登录界面，否则执行 block Lambda 中的代码
 */
fun ComponentActivity.ifLoginThen(block: (userBasicInfo: UserBasicInfo) -> Unit) {
    lifecycleScope.launchWhenCreated {
        checkToken {
            when (val userBasicInfo = it.getOrNull()) {
                null -> tryShowLoginDialog()
                else -> block.invoke(userBasicInfo)
            }
        }
    }
}

interface Login {

    fun loginAfter(block: (() -> UserBasicInfo))
}

fun ifLogin(action: (UserBasicInfo) -> Unit) = object : Login {
    override fun loginAfter(block: () -> UserBasicInfo) {
        action.invoke(block.invoke())
    }
}

suspend infix fun Login.otherwise(that: () -> Unit) {
    checkToken {
        when (val userBasicInfo = it.getOrNull()) {
            null -> that.invoke()
            else -> loginAfter { userBasicInfo }
        }
    }
}

fun Activity.tryShowLoginDialog() {
    takeUnless { isShowing.get() }?.let { showLoginDialog() }
}

private fun Context.showLoginDialog() {
    MessageDialog.Builder(this)
        .setTitle("系统消息")
        .setMessage("账号未登录，是否登录？")
        .setConfirm("现在登录")
        .setCancel("暂不登录")
        .addOnShowListener {
            isShowing.set(true)
        }
        .addOnDismissListener {
            isShowing.set(false)
        }
        .addOnCancelListener {
            isShowing.set(false)
        }
        .setListener {
            LoginActivity.start(this, UserManager.getCurrLoginAccount(), UserManager.getCurrLoginAccountPassword())
        }.show()
}