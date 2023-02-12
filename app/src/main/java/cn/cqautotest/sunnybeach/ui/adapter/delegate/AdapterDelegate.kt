package cn.cqautotest.sunnybeach.ui.adapter.delegate

import android.animation.Animator
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.OnItemClickListener
import cn.cqautotest.sunnybeach.ktx.OnItemLongClickListener
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.chad.library.adapter.base.animation.BaseAnimation

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 适配器代理
 */
class AdapterDelegate {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    var adapterAnimation: BaseAnimation? = null
    private var mLastPosition = -1

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        addAnimation(holder)
    }

    private fun addAnimation(holder: RecyclerView.ViewHolder) {
        if (holder.layoutPosition <= mLastPosition) return
        val animation: BaseAnimation = adapterAnimation ?: AlphaInAnimation()
        animation.animators(holder.itemView).forEach { startAnim(it) }
        mLastPosition = holder.layoutPosition
    }

    private fun startAnim(anim: Animator) {
        anim.start()
    }

    fun onItemClick(v: View, position: Int) {
        mOnItemClickListener?.onItemClick(v, position)
    }

    fun onItemLongClick(v: View, position: Int) = mOnItemLongClickListener?.onItemLongClick(v, position) ?: false

    fun setOnItemClickListener(block: OnItemClickListener?) {
        mOnItemClickListener = block
    }

    fun setOnItemLongClickListener(block: OnItemLongClickListener?) {
        mOnItemLongClickListener = block
    }
}