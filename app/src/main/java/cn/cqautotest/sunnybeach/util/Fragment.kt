package cn.cqautotest.sunnybeach.util

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.startActivity() =
    requireContext().startActivity<T>()