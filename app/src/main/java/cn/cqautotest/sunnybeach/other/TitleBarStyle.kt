package cn.cqautotest.sunnybeach.other

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import cn.cqautotest.sunnybeach.R
import com.hjq.bar.style.RippleBarStyle

/**
 *    author : Android 轮子哥 & A Lonely Cat
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2021/02/27
 *    desc   : 标题栏初始器
 */
class TitleBarStyle : RippleBarStyle() {

    override fun getTitleBarBackground(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.shape_gradient)
    }
}