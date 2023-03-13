package cn.cqautotest.sunnybeach.action

import androidx.annotation.StringRes
import com.hjq.toast.Toaster

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/08
 *    desc   : 吐司意图
 */
interface ToastAction {

    fun toast(text: CharSequence?) {
        Toaster.show(text)
    }

    fun toast(@StringRes id: Int) {
        Toaster.show(id)
    }

    fun toast(`object`: Any?) {
        Toaster.show(`object`)
    }
}