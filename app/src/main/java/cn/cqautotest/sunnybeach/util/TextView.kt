package cn.cqautotest.sunnybeach.util

import android.widget.TextView

fun TextView.clearText() {
    text = null
}

val TextView.textString
    get() = text.toString()