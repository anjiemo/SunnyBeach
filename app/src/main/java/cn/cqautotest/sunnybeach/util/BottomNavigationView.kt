package cn.cqautotest.sunnybeach.util

import android.view.View
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.clearLongClickTips() {
    val menu = menu
    for (index in 0 until menu.size) {
        val menuItem = menu[index]
        findViewById<View>(menuItem.itemId).setOnLongClickListener { true }
    }
}