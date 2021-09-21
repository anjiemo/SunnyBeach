package cn.cqautotest.sunnybeach.ui.adapter

import android.animation.Animator
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.chad.library.adapter.base.animation.BaseAnimation

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/9/6
 * desc   : 适配器代理
 */
class AdapterDelegate {

    var adapterAnimation: BaseAnimation? = null
    private var mLastPositon = -1

    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        addAnimation(holder)
    }

    private fun addAnimation(holder: RecyclerView.ViewHolder) {
        if (holder.layoutPosition <= mLastPositon) return
        val animation: BaseAnimation = adapterAnimation ?: AlphaInAnimation()
        animation.animators(holder.itemView).forEach {
            startAnim(it)
        }
        mLastPositon = holder.layoutPosition
    }

    private fun startAnim(anim: Animator) {
        anim.start()
    }
}