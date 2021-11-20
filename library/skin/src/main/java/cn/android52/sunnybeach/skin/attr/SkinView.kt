package cn.android52.sunnybeach.skin.attr

import android.view.View

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤 View，包括需要换肤的 View 以及需要换肤的皮肤属性信息
 */
class SkinView(private val view: View, private val attrs: List<SkinAttr>) {
    fun apply() {
        attrs.forEach {
            it.apply(view)
        }
    }
}