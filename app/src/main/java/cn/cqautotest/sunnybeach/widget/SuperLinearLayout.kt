package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import cn.cqautotest.sunnybeach.R
import com.hjq.shape.layout.ShapeLinearLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 支持设置分割线宽度的 LinearLayout
 */
class SuperLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ShapeLinearLayout(context, attrs) {

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SuperLinearLayout).apply {
            dividerDrawable = GradientDrawable().apply {
                (dividerDrawable as? ColorDrawable)?.let { setColor(it.color) }
                // The height of the divider has no meaning to be set, please use the dividerPadding property to limit it.
                setSize(getDimensionPixelSize(R.styleable.SuperLinearLayout_dividerWidth, -1), 0)
            }

        }.also {
            it.recycle()
        }
    }
}
