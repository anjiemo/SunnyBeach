package cn.cqautotest.sunnybeach.util

import androidx.appcompat.widget.TooltipCompat
import com.google.android.material.tabs.TabLayout

/**
 * Should be after updating the adapter call, otherwise clearing the tooltip text will have no effect.
 */
fun TabLayout.clearTooltipText() {
    val tabCount = tabCount
    repeat(tabCount) {
        val tabView = getTabAt(it)?.view ?: return@repeat
        TooltipCompat.setTooltipText(tabView, null)
    }
}

fun TabLayout.onTabSelected(block: (tab: TabLayout.Tab?) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            block.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {}
    })
}