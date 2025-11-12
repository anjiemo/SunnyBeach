package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/10/05
 * desc   : RadioGroup 基类封装
 */
open class BaseRadioGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : Group(context, attrs) {

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        getViews()?.forEach { it?.setOnClickListener(l) }
    }

    open fun getViews() = (parent as? ViewGroup)?.let { parent -> referencedIds.map { parent.findViewById<View?>(it) } }

    open fun dispatchSelect(isSelected: Boolean) {
        getViews()?.forEach { it?.isSelected = isSelected }
    }
}