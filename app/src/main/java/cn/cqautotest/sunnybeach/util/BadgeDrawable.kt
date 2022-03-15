package cn.cqautotest.sunnybeach.util

import android.content.Context
import android.graphics.Color
import com.google.android.material.badge.BadgeDrawable

fun createDefaultStyleBadge(context: Context, unReadCount: Int) =
    BadgeDrawable.create(context).apply {
        if (unReadCount <= 0) {
            isVisible = unReadCount != 0
        } else {
            number = unReadCount
        }
        badgeGravity = BadgeDrawable.TOP_END
        badgeTextColor = Color.WHITE
        backgroundColor = Color.RED
        verticalOffset = 20
        horizontalOffset = 20
    }