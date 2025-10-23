package cn.cqautotest.sunnybeach.util

import android.app.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class DialogLeaksCleaner(private val owner: LifecycleOwner, private val dialog: Dialog) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val unregisterObserver: () -> Unit = {
            owner.lifecycle.removeObserver(this)
        }
        if (event != Lifecycle.Event.ON_DESTROY) return
        if (!dialog.isShowing) return unregisterObserver.invoke()
        unregisterObserver.invoke()
        dialog.dismiss()
    }
}