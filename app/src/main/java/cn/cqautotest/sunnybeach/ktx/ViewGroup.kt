package cn.cqautotest.sunnybeach.ktx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup?.inflate(@LayoutRes layoutId: Int): View? {
    if (this == null) {
        return null
    }
    val inflater = LayoutInflater.from(context)
    return inflater.inflate(layoutId, this, false)
}