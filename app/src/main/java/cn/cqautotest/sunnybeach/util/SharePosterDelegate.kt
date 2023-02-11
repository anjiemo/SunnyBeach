package cn.cqautotest.sunnybeach.util

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.isInvisible
import cn.cqautotest.sunnybeach.ktx.waitViewDrawFinished
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ViewUtils

abstract class SharePosterDelegate(private val viewGroup: ViewGroup) {

    suspend fun inflateView(@LayoutRes layoutId: Int) {
        val shareLayout = ViewUtils.layoutId2View(layoutId).also { it.isInvisible = true }
        // 设置数据
        updateViewConfig(shareLayout)
        // 临时添加到 ViewGroup 中
        viewGroup.addView(shareLayout)
        // 更新 LayoutParams
        shareLayout.updateViewLayoutParams()
        // 等待 View 绘制完毕
        shareLayout.waitViewDrawFinished()
        // 把 View 转成 Bitmap
        val bitmap = ImageUtils.view2Bitmap(shareLayout)
        // 从 ViewGroup 中移除
        viewGroup.removeView(shareLayout)
        onComplete(bitmap)
    }

    abstract fun updateViewConfig(view: View)

    abstract fun View.updateViewLayoutParams()

    abstract suspend fun onComplete(bitmap: Bitmap?)
}
