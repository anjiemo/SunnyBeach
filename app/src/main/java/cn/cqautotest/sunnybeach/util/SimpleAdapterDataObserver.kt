package cn.cqautotest.sunnybeach.util

import androidx.recyclerview.widget.RecyclerView

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/27
 * desc   : 简单 RecyclerView.AdapterDataObserver
 */
class SimpleAdapterDataObserver(private val onChanged: () -> Unit) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        onChanged.invoke()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        onChanged()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        onChanged()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        onChanged()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        onChanged()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        onChanged()
    }
}