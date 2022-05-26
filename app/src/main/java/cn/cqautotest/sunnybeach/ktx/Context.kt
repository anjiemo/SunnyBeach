package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater

fun Context.asInflater() = LayoutInflater.from(this)

inline fun <reified T : Activity> Context.startActivity(noinline block: (Intent.() -> Unit)? = null) {
    Intent(this, T::class.java).also {
        if (this !is Activity) it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(it.apply { block?.invoke(this) })
    }
}
