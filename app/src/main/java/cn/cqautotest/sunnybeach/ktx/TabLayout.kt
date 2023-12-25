package cn.cqautotest.sunnybeach.ktx

import androidx.appcompat.widget.TooltipCompat
import com.google.android.material.tabs.TabLayout

/**
 * Should be after updating the adapter call, otherwise clearing the tooltip text will have no effect.
 */
fun TabLayout.clearTooltipText() {
    repeat(tabCount) {
        getTabAt(it)?.view?.run { TooltipCompat.setTooltipText(this, null) }
    }
}

fun TabLayout.doTabSelected(block: (tab: TabLayout.Tab) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            block.invoke(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}

        override fun onTabReselected(tab: TabLayout.Tab) {}
    })
}