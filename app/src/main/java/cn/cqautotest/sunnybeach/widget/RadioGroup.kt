package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import cn.cqautotest.sunnybeach.R

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/10/05
 * desc   : RadioGroup 单选按钮组
 */
class RadioGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseRadioGroup(context, attrs) {

    // 默认选中的 id
    private val defaultSelectId: Int

    // 是否初始化
    private var isInitialization = false

    // 选中的监听
    var onSelected: ((View) -> Unit) = {}

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.RadiusGroup)
        defaultSelectId = attributes.getResourceId(R.styleable.RadiusGroup_defaultSelectId, View.NO_ID)
        attributes.recycle()
    }

    override fun applyLayoutFeatures() {
        super.applyLayoutFeatures()
        if (defaultSelectId != View.NO_ID && !isInitialization) {
            selected(defaultSelectId)
        }
        getViews()?.forEach {
            it?.setOnClickListener { _ ->
                dispatchSelect(false)
                it.isSelected = true
                onSelected.invoke(it)
            }
        }
    }

    /**
     * 选中某个 View，参数：id
     */
    fun selected(id: Int) {
        dispatchSelect(false)
        getViews()?.find { it.id == id }?.also { it.isSelected = true }?.also(onSelected)
        isInitialization = true
    }
}
