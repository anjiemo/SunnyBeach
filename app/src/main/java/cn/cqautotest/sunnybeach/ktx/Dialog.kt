package cn.cqautotest.sunnybeach.ktx

import android.app.Dialog
import androidx.lifecycle.LifecycleOwner
import cn.cqautotest.sunnybeach.util.BaseDialogLeaksCleaner
import cn.cqautotest.sunnybeach.util.DialogLeaksCleaner
import com.hjq.base.BaseDialog

fun <T : BaseDialog.Builder<*>> T.bindLeaksCleaner(): T {
    val owner = getContext()
    if (owner !is LifecycleOwner) return this
    owner.lifecycle.addObserver(BaseDialogLeaksCleaner(owner = owner, closeable = this))
    return this
}

fun <T : Dialog> T.bindLeaksCleaner(): T {
    val owner = context
    if (owner !is LifecycleOwner) return this
    owner.lifecycle.addObserver(DialogLeaksCleaner(owner = owner, dialog = this))
    return this
}