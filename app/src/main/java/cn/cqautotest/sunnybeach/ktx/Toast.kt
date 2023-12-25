package cn.cqautotest.sunnybeach.ktx

import com.hjq.toast.Toaster

fun simpleToast(text: Int) {
    Toaster.show(text)
}

fun simpleToast(text: CharSequence) {
    Toaster.show(text)
}