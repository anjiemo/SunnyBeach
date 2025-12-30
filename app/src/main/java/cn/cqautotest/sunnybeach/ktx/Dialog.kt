package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.app.Dialog
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner
import cn.cqautotest.sunnybeach.util.BaseDialogLeaksCleaner
import cn.cqautotest.sunnybeach.util.DialogLeaksCleaner
import com.hjq.base.BaseDialog

/**
 * 查找 Dialog 绑定的 Activity。
 * 通常情况下，Context 的包装不会超过 3-4层，我们最多查找 10 层以防止死循环导致 ANR。
 * @param maxDepth 最大查找层数
 */
fun Dialog.findAttachedActivity(maxDepth: Int = 10): Activity? {
    return generateSequence(context) { (it as? ContextWrapper)?.baseContext }
        .take(maxDepth) // 最多查找 maxDepth 层，防止死循环导致 ANR
        .filterIsInstance<Activity>()
        .firstOrNull()
}

/**
 * 获取 Dialog 绑定的 Activity，并判断 Activity 是否存活，如果 Activity 处于销毁中则返回 null，否则返回 Activity。
 */
fun Dialog.findAttachedSafeActivity(): Activity? {
    return findAttachedActivity()
        ?.takeUnless { it.isFinishing || it.isDestroyed }
}

fun <T : BaseDialog.Builder<*>> T.bindLeaksCleaner(): T {
    val owner = getContext()
    if (owner !is LifecycleOwner) return this
    owner.lifecycle.addObserver(BaseDialogLeaksCleaner(owner = owner, closeable = this))
    return this
}

fun <T : Dialog> T.bindLeaksCleaner(): T {
    val owner = findAttachedActivity()
    if (owner !is LifecycleOwner) return this
    owner.lifecycle.addObserver(DialogLeaksCleaner(owner = owner, dialog = this))
    return this
}