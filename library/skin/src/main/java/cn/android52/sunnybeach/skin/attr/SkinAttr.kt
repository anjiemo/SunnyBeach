package cn.android52.sunnybeach.skin.attr

import android.view.View

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤属性
 */
class SkinAttr(private val resName: String, private val resType: String, var type: SkinAttrType) {

    fun apply(view: View?) {
        type.apply(view, resType, resName)
    }
}