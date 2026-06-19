package cn.cqautotest.sunnybeach.ktx

import androidx.fragment.app.Fragment
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import com.dylanc.longan.viewLifecycleScope

/**
 * 检查是否用户登录
 * 如果没有登录则跳转至登录界面，否则执行 block Lambda 中的代码
 */
fun Fragment.ifLoginThen(block: (userBasicInfo: UserBasicInfo) -> Unit) {
    viewLifecycleScope.launchWhenCreated {
        checkToken {
            when (val userBasicInfo = it.getOrNull()) {
                null -> requireActivity().tryShowLoginDialog()
                else -> block.invoke(userBasicInfo)
            }
        }
    }
}

/**
 * 检查当前 Fragment 的 UI 环境是否失效
 *
 * 适用于各种异步回调（如 view.post、网络请求返回、协程挂起恢复后），
 * 判断此时执行 UI 相关的逻辑或调用 requireActivity() 等是否安全。
 *
 * @return true 表示 UI 已经失效（Activity 已不存在、Fragment 被分离、或 View 被销毁），应立即终止 UI 操作。
 */
val Fragment.isUiInvalid: Boolean
    get() = activity == null || isDetached || view == null
