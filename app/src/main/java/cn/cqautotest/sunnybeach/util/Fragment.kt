package cn.cqautotest.sunnybeach.util

import androidx.fragment.app.Fragment
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import com.dylanc.longan.viewLifecycleScope

/**
 * 解析当前用户的 Token
 */
fun Fragment.checkToken(block: suspend (Result<UserBasicInfo>) -> Unit) {
    viewLifecycleScope.launchWhenCreated {
        when (val result = Repository.checkToken()) {
            null -> block.invoke(Result.failure(NotLoginException()))
            else -> block.invoke(Result.success(result))
        }
    }
}

/**
 * 检查是否用户登录
 * 如果没有登录则跳转至登录界面，否则执行 block Lambda 中的代码
 */
private var isShowing = false
fun Fragment.takeIfLogin(block: suspend (userBasicInfo: UserBasicInfo) -> Unit) {
    checkToken {
        when (val userBasicInfo = it.getOrNull()) {
            null -> {
                if (isShowing) {
                    return@checkToken
                }
                MessageDialog.Builder(requireContext())
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
                        LoginActivity.start(requireContext(), "", "")
                    }.show()
            }
            else -> block.invoke(userBasicInfo)
        }
    }
}