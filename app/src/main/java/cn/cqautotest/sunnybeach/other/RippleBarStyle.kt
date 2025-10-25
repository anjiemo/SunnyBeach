package cn.cqautotest.sunnybeach.other

import android.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import com.hjq.bar.TitleBarSupport
import com.hjq.bar.style.TransparentBarStyle

/**
 * author : Android 轮子哥 & A Lonely Cat
 * github : https://github.com/getActivity/TitleBar
 * time   : 2020/09/19
 * desc   : 水波纹样式实现（对应布局属性：app:barStyle="ripple"）
 */
open class RippleBarStyle : TransparentBarStyle() {
    override fun getLeftTitleBackground(context: Context): Drawable? {
        return createRippleDrawable(context) ?: super.getLeftTitleBackground(context)
    }

    override fun getRightTitleBackground(context: Context): Drawable? {
        return createRippleDrawable(context) ?: super.getLeftTitleBackground(context)
    }

    /**
     * 获取水波纹的点击效果
     */
    fun createRippleDrawable(context: Context): Drawable? {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, typedValue, true)) {
            return TitleBarSupport.getDrawable(context, typedValue.resourceId)
        }
        return null
    }
}