package cn.cqautotest.sunnybeach.ktx

import com.hjq.toast.ToastUtils

fun simpleToast(text: Int) {
    ToastUtils.show(text)
}

fun simpleToast(text: CharSequence) {
    ToastUtils.show(text)
}