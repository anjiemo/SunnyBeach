package cn.cqautotest.sunnybeach.ktx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

inline fun <reified T : ViewBinding> ViewGroup.asViewBinding(): T {
    val method = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    return method.invoke(this, asInflate(), this, false) as T
}

fun ViewGroup?.inflate(@LayoutRes layoutId: Int): View? =
    takeIf { this != null }?.context?.asInflater()?.inflate(layoutId, this, false)

inline fun ViewGroup.doOnChildViewAdded(crossinline action: (parent: View, child: View) -> Unit) {
    doOnHierarchyChange(onAdded = action)
}

inline fun ViewGroup.doOnChildViewRemoved(crossinline action: (parent: View, child: View) -> Unit) {
    doOnHierarchyChange(onRemoved = action)
}

inline fun ViewGroup.doOnHierarchyChange(
    crossinline onAdded: (parent: View, child: View) -> Unit = { _, _ -> },
    crossinline onRemoved: (parent: View, child: View) -> Unit = { _, _ -> }
) {
    setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
        override fun onChildViewAdded(parent: View, child: View) = onAdded(parent, child)
        override fun onChildViewRemoved(parent: View, child: View) = onRemoved(parent, child)
    })
}