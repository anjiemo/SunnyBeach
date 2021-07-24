package cn.cqautotest.sunnybeach.util

import androidx.fragment.app.Fragment
import cn.cqautotest.sunnybeach.app.AppApplication

inline fun <reified T> Fragment.startActivity() =
    AppApplication.getInstance().startActivity<T>()