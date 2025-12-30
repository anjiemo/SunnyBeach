package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.app.Dialog
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner
import cn.cqautotest.sunnybeach.util.BaseDialogLeaksCleaner
import cn.cqautotest.sunnybeach.util.DialogLeaksCleaner
import com.hjq.base.BaseDialog

/**
 * 查找 Dialog 绑定的 Activity
 */
fun Dialog.findAttachedActivity(): Activity? {
    var context = context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
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