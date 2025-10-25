package cn.cqautotest.sunnybeach.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class BaseDialogLeaksCleaner(private val owner: LifecycleOwner, private val closeable: AutoCloseable) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val unregisterObserver: () -> Unit = {
            owner.lifecycle.removeObserver(this)
        }
        if (event != Lifecycle.Event.ON_DESTROY) return
        unregisterObserver.invoke()
        closeable.close()
    }
}