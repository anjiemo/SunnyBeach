package cn.cqautotest.sunnybeach.ui.adapter.delegate

import cn.cqautotest.sunnybeach.widget.SimpleGridLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/28
 * desc   : 九宫格图片适配器的代理
 */
class NineGridAdapterDelegate : SimpleGridLayout.OnNineGridClickListener {

    private var mOnNineGridClickListener: SimpleGridLayout.OnNineGridClickListener? = null

    fun setOnNineGridItemClickListener(listener: SimpleGridLayout.OnNineGridClickListener) {
        mOnNineGridClickListener = listener
    }

    override fun onNineGridItemClick(sources: List<String>, index: Int) {
        mOnNineGridClickListener?.onNineGridItemClick(sources, index)
    }
}