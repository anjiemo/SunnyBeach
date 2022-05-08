package cn.cqautotest.sunnybeach.ktx

import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.doPageSelected(onPageSelected: (position: Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }
    })
}