@file:JvmName("ViewUtils")

package cn.cqautotest.sunnybeach.ktx

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.collection.SparseArrayCompat
import androidx.collection.set
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.TouchUtils
import com.dylanc.longan.rootWindowInsetsCompat
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

fun View?.getLocationInWindowPoint(): Point {
    this ?: return Point()
    val l = IntArray(2).apply(::getLocationInWindow)
    return Point(l[0], l[1])
}

fun View?.getLocationOnScreenPoint(): Point {
    this ?: return Point()
    val l = IntArray(2).apply(::getLocationOnScreen)
    return Point(l[0], l[1])
}

@RequiresApi(Build.VERSION_CODES.Q)
fun View?.getLocationInSurfacePoint(): Point {
    this ?: return Point()
    val l = IntArray(2).apply(::getLocationInSurface)
    return Point(l[0], l[1])
}

fun View?.getLocationInWindowRect(): Rect {
    this ?: return Rect()
    val l = IntArray(2).apply(::getLocationInWindow)
    return Rect(l[0], l[1], l[0] + measuredWidth, l[1] + measuredHeight)
}

fun View?.getLocationOnScreenRect(): Rect {
    this ?: return Rect()
    val l = IntArray(2).apply(::getLocationOnScreen)
    return Rect(l[0], l[1], l[0] + measuredWidth, l[1] + measuredHeight)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun View?.getLocationInSurfaceRect(): Rect {
    this ?: return Rect()
    val l = IntArray(2).apply(::getLocationInSurface)
    return Rect(l[0], l[1], l[0] + measuredWidth, l[1] + measuredHeight)
}

suspend fun View?.waitViewDrawFinished() {
    this ?: return
    suspendCancellableCoroutine { continuation ->
        post { continuation.resume(Unit) }
    }
}

// BadgeDrawable 内部是弱引用持有 View，我们不关心 View 的释放问题
private val mDrawableCacheMap = SparseArrayCompat<BadgeDrawable>()

@SuppressLint("UnsafeOptInUsageError")
fun View.setUnReadCount(unReadCount: Int) {
    val anchor = this
    val viewId = hashCode()
    // 通过 View 的 id 从缓存里获取 BadgeDrawable
    val drawable = mDrawableCacheMap[viewId]
    if (drawable == null) {
        // 如果缓存里没有，则创建 BadgeDrawable
        createDefaultStyleBadge(context, unReadCount).apply {
            BadgeUtils.attachBadgeDrawable(this, anchor)
            mDrawableCacheMap[viewId] = this
        }
    } else {
        // 否则更新 BadgeDrawable 的状态
        drawable.isVisible = unReadCount > 0
        drawable.number = unReadCount
    }
}

fun View.asInflate(): LayoutInflater = context.asInflater()

/**
 * 请求获取软键盘的高度
 */
@Px
fun View.requireKeyboardHeight(): Int {
    val insets = rootWindowInsetsCompat?.getInsets(WindowInsetsCompat.Type.ime()) ?: return 0
    return insets.bottom - insets.top
}

/**
 * 设置哀悼风格
 */
fun View.setMourningStyle() {
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    paint.colorFilter = ColorMatrixColorFilter(cm)
    setLayerType(View.LAYER_TYPE_HARDWARE, paint)
}

/**
 * 清除哀悼风格
 */
fun View.removeMourningStyle() {
    setLayerType(View.LAYER_TYPE_NONE, Paint())
}

@JvmOverloads
fun View.setRoundRectBg(color: Int = Color.WHITE, @Px cornerRadius: Int = 15.dp) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
    }
}

private var lastClickTime: Long = 0
private val jumpTapTimeout = ViewConfiguration.getJumpTapTimeout()

fun View.setFixOnClickListener(delayTime: Int = jumpTapTimeout, block: (v: View) -> Unit) {
    setOnClickListener {
        val currentClickTime = System.currentTimeMillis()
        val diffTime = currentClickTime - lastClickTime
        if (diffTime > delayTime) {
            block.invoke(this)
        }
        lastClickTime = currentClickTime
    }
}

fun View.setDoubleClickListener(block: (v: View) -> Unit) {
    setOnClickListener {
        val currentClickTime = System.currentTimeMillis()
        val diffTime = currentClickTime - lastClickTime
        if (diffTime < jumpTapTimeout) {
            block.invoke(this)
        }
        lastClickTime = currentClickTime
    }
}

fun View.setSlidingUpListener(block: (v: View) -> Unit) {
    object : TouchUtils.OnTouchUtilsListener() {
        override fun onDown(view: View?, x: Int, y: Int, event: MotionEvent?): Boolean {
            return true
        }

        override fun onMove(
            view: View?,
            direction: Int,
            x: Int,
            y: Int,
            dx: Int,
            dy: Int,
            totalX: Int,
            totalY: Int,
            event: MotionEvent?
        ): Boolean {
            return false
        }

        override fun onStop(
            view: View,
            direction: Int,
            x: Int,
            y: Int,
            totalX: Int,
            totalY: Int,
            vx: Int,
            vy: Int,
            event: MotionEvent?
        ): Boolean {
            block.invoke(view)
            return true
        }
    }
}
