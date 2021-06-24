package cn.cqautotest.sunnybeach.utils

import android.widget.Toast
import cn.cqautotest.sunnybeach.app.AppApplication

fun simpleToast(text: Int) {
    Toast.makeText(AppApplication.getInstance(), text, Toast.LENGTH_SHORT).show()
}

fun simpleToast(text: CharSequence) {
    Toast.makeText(AppApplication.getInstance(), text, Toast.LENGTH_SHORT).show()
}