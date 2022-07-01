package cn.cqautotest.sunnybeach.ktx

import android.view.View
import androidx.core.view.forEach
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.clearLongClickTips() = menu.forEach {
    findViewById<View>(it.itemId).setOnLongClickListener { true }
}