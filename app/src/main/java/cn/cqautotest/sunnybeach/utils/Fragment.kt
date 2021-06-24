package cn.cqautotest.sunnybeach.utils

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.startActivity() =
    requireContext().startActivity<T>()