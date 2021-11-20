package cn.cqautotest.sunnybeach.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

inline fun <reified T : ViewBinding> inflate(
    inflater: LayoutInflater,
    parent: ViewGroup? = null,
    attachToParent: Boolean = false
): T {
    val method = T::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        View::class.java,
        Boolean::class.java
    )
    return method.invoke(inflater, parent, attachToParent) as T
}

inline fun <reified T : ViewBinding> bind(rootView: View): T {
    val method = T::class.java.getMethod("bind", View::class.java)
    return method.invoke(rootView) as T
}