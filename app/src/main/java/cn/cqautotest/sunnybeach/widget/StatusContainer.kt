package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import cn.cqautotest.sunnybeach.action.StatusAction

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 状态布局容器（网络错误，异常错误，空数据）
 */
class StatusContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), StatusAction {

    private val mStatusLayout by lazy { StatusLayout(context, attrs) }

    init {
        addView(mStatusLayout)
    }

    override fun getStatusLayout(): StatusLayout = mStatusLayout
}