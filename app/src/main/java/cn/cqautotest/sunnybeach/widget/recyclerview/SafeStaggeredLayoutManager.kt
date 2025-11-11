package cn.cqautotest.sunnybeach.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/11/11
 * desc   : 修复 StaggeredGridLayoutManager 导致的崩溃问题
 */
class SafeStaggeredLayoutManager : StaggeredGridLayoutManager {

    private var mIsDetachedFromWindow = false

    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: Exception) {
            Timber.e("onLayoutChildren：e ${e.message}")
        }
    }
}