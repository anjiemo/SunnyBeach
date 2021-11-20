package cn.android52.sunnybeach.skin.attr

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.android52.sunnybeach.skin.manager.SkinManager

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/11/19
 *    desc   : 皮肤属性类型
 */
enum class SkinAttrType(val resType: String) {

    /**
     * background
     */
    BACKGROUND("background") {
        override fun apply(view: View?, resType: String, resName: String) {
            val drawable: Drawable? = getResourceManager().getDrawableByName(resType, resName)
            if (view != null && drawable != null) {
                view.background = drawable
            }
        }
    },

    /**
     * src
     */
    SRC("src") {
        override fun apply(view: View?, resType: String, resName: String) {
            val drawable: Drawable? = getResourceManager().getDrawableByName(resType, resName)
            if (view is ImageView && drawable != null) {
                view.setImageDrawable(drawable)
            }
        }
    },

    /**
     * textColor
     */
    TEXT_COLOR("textColor") {
        override fun apply(view: View?, resType: String, resName: String) {
            val colorStateList: ColorStateList? =
                getResourceManager().getColorByResName(resType, resName)
            if (view is TextView && colorStateList != null) {
                view.setTextColor(colorStateList)
            }
        }
    };

    fun getResourceManager() = SkinManager.instance.resourcesManager

    abstract fun apply(view: View?, resType: String, resName: String)
}