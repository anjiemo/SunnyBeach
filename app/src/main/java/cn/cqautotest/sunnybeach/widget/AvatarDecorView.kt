package cn.cqautotest.sunnybeach.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scaleMatrix
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.http.glide.GlideApp
import cn.cqautotest.sunnybeach.util.dp
import com.blankj.utilcode.util.ImageUtils

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/03/31
 *    desc   : 头像装饰控件
 */
class AvatarDecorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val defaultAvatar = ContextCompat.getDrawable(context, R.mipmap.ic_default_avatar)
    var borderDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setStroke(1.dp, ContextCompat.getColor(context, R.color.pink))
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = ImageUtils.getBitmap(R.drawable.ic_flash)
    private val scaleFactor = 0.22f
    private val scaleMatrix = scaleMatrix(scaleFactor, scaleFactor)

    var isVip: Boolean = false

    init {
        if (drawable == null) setImageDrawable(defaultAvatar)
    }

    fun loadAvatar(vip: Boolean = false, resource: Any?, block: GradientDrawable.() -> Unit = {}) {
        isVip = vip
        block.invoke(borderDrawable)
        GlideApp.with(this)
            .load(resource)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(this)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        val decorDrawable = if (isVip && drawable != null) {
            LayerDrawable(arrayOf(drawable, borderDrawable))
        } else {
            drawable ?: defaultAvatar
        }
        super.setImageDrawable(decorDrawable)
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        canvas ?: return
        if (isVip) {
            // Timber.d("onDrawForeground：===> width is $width measuredWidth is $measuredWidth")
            // 画布默认是在左上角，需要先调整画布的位置
            val dx = width - bitmap.width * scaleFactor
            val dy = height - bitmap.height * scaleFactor
            canvas.translate(dx, dy)
            canvas.drawBitmap(bitmap, scaleMatrix, paint)
        }
    }
}