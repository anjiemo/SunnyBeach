package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import cn.cqautotest.sunnybeach.R

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/10
 * desc   : 列表菜单容器
 */
class ListMenuItemContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val viewContainer =
        LayoutInflater.from(context).inflate(R.layout.list_menu_item, this, true)
    val llComment: View by lazy { viewContainer.findViewById(R.id.ll_comment) }
    val ivComment: ImageView by lazy { viewContainer.findViewById(R.id.iv_comment) }
    val tvComment: TextView by lazy { viewContainer.findViewById(R.id.tv_comment) }

    val llGreat: View by lazy { viewContainer.findViewById(R.id.ll_great) }
    val ivGreat: ImageView by lazy { viewContainer.findViewById(R.id.iv_great) }
    val tvGreat: TextView by lazy { viewContainer.findViewById(R.id.tv_great) }

    val ivShare: ImageView by lazy { viewContainer.findViewById(R.id.iv_share) }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ListMenuItemContainer)
        val firstIcon = ta.getDrawable(R.styleable.ListMenuItemContainer_defaultFirstIcon)
        val secondIcon = ta.getDrawable(R.styleable.ListMenuItemContainer_defaultSecondIcon)
        val shareIcon = ta.getDrawable(R.styleable.ListMenuItemContainer_defaultShareIcon)
        val firstText = ta.getString(R.styleable.ListMenuItemContainer_defaultFirstText)
        val secondText = ta.getString(R.styleable.ListMenuItemContainer_defaultSecondText)
        firstIcon?.let {
            ivComment.setImageDrawable(firstIcon)
        }
        secondIcon?.let {
            ivGreat.setImageDrawable(secondIcon)
        }
        shareIcon?.let {
            ivShare.setImageDrawable(shareIcon)
        }
        firstText?.let {
            tvComment.text = firstText
        }
        secondText?.let {
            tvGreat.text = secondText
        }
        ta.recycle()
        // 偷梁换柱，去掉一层布局嵌套
        (parent as? ViewGroup)?.let {
            val view = (viewContainer as ViewGroup).getChildAt(0)
            view.id = id
            val index = it.indexOfChild(this)
            it.removeViewAt(index)
            (view.parent as ViewGroup).removeAllViews()
            it.addView(view, index)
        }
    }
}