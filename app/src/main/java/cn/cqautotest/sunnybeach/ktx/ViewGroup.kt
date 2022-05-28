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