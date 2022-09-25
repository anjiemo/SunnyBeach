package cn.cqautotest.sunnybeach.ktx

import androidx.fragment.app.Fragment
import cn.cqautotest.sunnybeach.model.UserBasicInfo

/**
 * 检查是否用户登录
 * 如果没有登录则跳转至登录界面，否则执行 block Lambda 中的代码
 */
fun Fragment.ifLoginThen(block: (userBasicInfo: UserBasicInfo) -> Unit) {
    checkToken {
        when (val userBasicInfo = it.getOrNull()) {
            null -> requireActivity().tryShowLoginDialog()
            else -> block.invoke(userBasicInfo)
        }
    }
}
