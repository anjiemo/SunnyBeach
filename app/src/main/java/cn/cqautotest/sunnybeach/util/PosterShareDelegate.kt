package cn.cqautotest.sunnybeach.util

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.isInvisible
import cn.cqautotest.sunnybeach.ktx.waitViewDrawFinished
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ViewUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/02/11
 * desc   : 海报分享代理类
 */
class PosterShareDelegate(private val viewGroup: ViewGroup) {

    private var delegateImpl: PosterShareDelegateImpl? = null

    suspend fun launch(@LayoutRes layoutId: Int) {
        val shareLayout = ViewUtils.layoutId2View(layoutId).also { it.isInvisible = true }
        // 设置数据
        delegateImpl?.updateViewConfig(shareLayout)
        // 临时添加到 ViewGroup 中
        viewGroup.addView(shareLayout)
        // 更新 LayoutParams
        delegateImpl?.updateViewLayoutParams(shareLayout)
        // 等待 View 绘制完毕
        shareLayout.waitViewDrawFinished()
        // 把 View 转成 Bitmap
        val bitmap = ImageUtils.view2Bitmap(shareLayout)
        // 从 ViewGroup 中移除
        viewGroup.removeView(shareLayout)
        delegateImpl?.onComplete(bitmap)
    }

    fun setOnPosterShareListener(init: PosterShareDelegateImpl.() -> Unit): PosterShareDelegate {
        val impl = PosterShareDelegateImpl()
        impl.init()
        delegateImpl = impl
        return this
    }

    class PosterShareDelegateImpl : PosterShareAction {

        private var onUpdateViewConfig: ((view: View) -> Unit)? = null
        private var onUpdateViewLayoutParams: ((View) -> Unit)? = null
        private var onComplete: (suspend (bitmap: Bitmap?) -> Unit)? = null

        fun updateViewConfig(block: (view: View) -> Unit) {
            onUpdateViewConfig = block
        }

        fun updateViewLayoutParams(block: View.() -> Unit) {
            onUpdateViewLayoutParams = block
        }

        fun onComplete(block: suspend (bitmap: Bitmap?) -> Unit) {
            onComplete = block
        }

        override fun updateViewConfig(view: View) {
            onUpdateViewConfig?.invoke(view)
        }

        override fun updateViewLayoutParams(view: View) {
            onUpdateViewLayoutParams?.invoke(view)
        }

        override suspend fun onComplete(bitmap: Bitmap?) {
            onComplete?.invoke(bitmap)
        }
    }

    interface PosterShareAction {

        fun updateViewConfig(view: View)

        fun updateViewLayoutParams(view: View)

        suspend fun onComplete(bitmap: Bitmap?)
    }
}
