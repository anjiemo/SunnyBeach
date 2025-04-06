package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.orEmpty
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.coroutines.Runnable

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/22
 * desc   : 简单宫格视图
 */
class SimpleGridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val photoContainer by lazy { findViewById<ViewGroup>(R.id.gl_photo_container) }
    private var mOnNineGridClickListener: OnNineGridClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.simple_grid_layout, this)
    }

    fun setData(images: List<String> = listOf()) = apply {
        isVisible = images.isNotEmpty()
        val task = Runnable {
            if (isAttachedToWindow.not()) {
                return@Runnable
            }
            photoContainer.forEachIndexed { index, childView ->
                val imageUrl = images.getOrNull(index).orEmpty()
                (childView as? ImageView)?.takeIf { childView.tag == "image" }?.let { imageView ->
                    if (imageUrl.isNotEmpty()) {
                        imageView.layoutParams.height = imageView.width
                        imageView.updateLayoutParams<MarginLayoutParams> {
                            bottomMargin = 4.dp
                        }
                        Glide.with(context)
                            .load(imageUrl)
                            .transform(MultiTransformation(CenterCrop(), RoundedCorners(6.dp)))
                            .into(imageView)
                    } else {
                        imageView.layoutParams.height = 0
                        imageView.updateLayoutParams<MarginLayoutParams> {
                            bottomMargin = 0
                        }
                    }
                    imageView.setFixOnClickListener { mOnNineGridClickListener?.onNineGridItemClick(images, index) }
                }
            }
        }
        post(task)
    }

    fun setOnNineGridClickListener(listener: OnNineGridClickListener?) = apply {
        mOnNineGridClickListener = listener
    }

    fun interface OnNineGridClickListener {
        fun onNineGridItemClick(sources: List<String>, index: Int)
    }
}