package cn.cqautotest.sunnybeach.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater

fun Context.asInflater() = LayoutInflater.from(this)

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val context = this
    Intent(this, T::class.java).apply(block).run {
        if (context !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(this)
    }
}
