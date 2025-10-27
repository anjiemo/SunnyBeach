package cn.cqautotest.sunnybeach.other

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.widget.TextView
import androidx.annotation.Px
import androidx.core.graphics.drawable.updateBounds
import com.airbnb.lottie.SimpleColorFilter
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/12/17
 * desc   : A simple implementation of TextViewTarget,
 * usually used when you need the image and text to be aligned top and bottom, and you can specify the size of the drawable.
 */
class TextViewTarget(private val textView: TextView, @param:Px private val drawableSize: Int = 0) :
    CustomViewTarget<TextView, Drawable>(textView) {

    private val defaultDrawable = ShapeDrawable(RectShape()).apply {
        updateBounds(right = drawableSize, bottom = drawableSize)
        setColorFilter(SimpleColorFilter(Color.TRANSPARENT))
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        errorDrawable?.updateBounds(right = drawableSize, bottom = drawableSize)
        textView.setCompoundDrawables(null, errorDrawable ?: defaultDrawable, null, null)
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        resource.updateBounds(right = drawableSize, bottom = drawableSize)
        textView.setCompoundDrawables(null, resource, null, null)
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        placeholder?.updateBounds(right = drawableSize, bottom = drawableSize)
        textView.setCompoundDrawables(null, placeholder ?: defaultDrawable, null, null)
    }
}