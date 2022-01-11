package cn.cqautotest.sunnybeach.util

import android.content.res.Resources
import android.util.TypedValue

/**
 * 简化 fori 循环
 */
fun Int.fori(action: (Int) -> Unit) = repeat(this) {
    action.invoke(it)
}

/**
 * 简化 forr 循环
 */
fun Int.forr(action: (Int) -> Unit) = repeat(this) {
    action.invoke(this - it)
}

val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
