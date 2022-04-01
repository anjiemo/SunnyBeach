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

    // 默认头像 drawable
    private val defAvatarDrawable = ContextCompat.getDrawable(context, R.mipmap.ic_default_avatar)

    // 边框 drawable
    private val decorDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setStroke(1.dp, ContextCompat.getColor(context, R.color.pink))
    }

    // 前景徽章图标的画笔
    private val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 小徽章 bitmap
    private val badgeBitmap = ImageUtils.getBitmap(R.drawable.ic_flash)

    // 缩放因子
    private val scaleFactor = 0.22f

    // 缩放矩阵
    private val scaleMatrix = scaleMatrix(scaleFactor, scaleFactor)

    // 是否为 VIP
    var isVip: Boolean = false

    init {
        // 如果 xml 里没有设置资源图片，则设置默认的头像 drawable
        if (drawable == null) setImageDrawable(defAvatarDrawable)
    }

    /**
     * 加载头像（剪裁成圆形）
     * 参数：
     * 1、是否为 VIP
     * 2、资源
     * 3、边框的自定义配置
     */
    fun loadAvatar(vip: Boolean = false, resource: Any?, block: GradientDrawable.() -> Unit = {}) {
        isVip = vip
        block.invoke(decorDrawable)
        GlideApp.with(this)
            .load(resource)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(this)
    }

    /**
     * 使用 Glide 加载 ImageView 图片时会调用此方法，我们可以在这里对设置的 drawable 资源进行修饰。
     */
    override fun setImageDrawable(drawable: Drawable?) {
        val decorDrawable = if (isVip && drawable != null) {
            // 如果是 VIP 且 drawable 资源不为空，则添加 decorDrawable
            LayerDrawable(arrayOf(drawable, decorDrawable))
        } else {
            // 如果设置的头像 drawable 为空，则设置默认头像 drawable
            drawable ?: defAvatarDrawable
        }
        super.setImageDrawable(decorDrawable)
    }

    /**
     * 绘制前景徽章图标
     */
    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        canvas ?: return
        if (isVip) {
            // Timber.d("onDrawForeground：===> width is $width measuredWidth is $measuredWidth")
            val dx = width - badgeBitmap.width * scaleFactor
            val dy = height - badgeBitmap.height * scaleFactor
            // 画布默认是在左上角，需要先调整画布的位置
            canvas.translate(dx, dy)
            // 绘制小徽章
            canvas.drawBitmap(badgeBitmap, scaleMatrix, badgePaint)
        }
    }
}