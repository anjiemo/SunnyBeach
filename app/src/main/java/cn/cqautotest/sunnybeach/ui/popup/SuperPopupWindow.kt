package cn.cqautotest.sunnybeach.ui.popup

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import cn.cqautotest.sunnybeach.action.Init
import com.dylanc.longan.windowInsetsControllerCompat

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/02/18
 * desc   : 支持系统复制、粘贴等菜单的 PopWindow
 */
open class SuperPopupWindow @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), Init, DialogInterface {

    private var mLayoutResId: Int = 0
    lateinit var contentView: View
        private set

    private val mOnShowObservers = mutableListOf<DialogInterface.OnShowListener>()
    private val mOnDismissObservers = mutableListOf<DialogInterface.OnDismissListener>()

    init {
        create()
    }

    private fun create() {
        setBackgroundColor(Color.parseColor("#99000000"))
        onCreate()
    }

    protected open fun onCreate() {

    }

    fun setContentView(@LayoutRes layoutId: Int) {
        removeAllViews()
        mLayoutResId = layoutId
        LayoutInflater.from(context).inflate(layoutId, this, false).also {
            addView(it)
            contentView = it
        }
        onViewCreated()
        callAllInit()
    }

    protected open fun onViewCreated() {

    }

    fun addOnShowListener(listener: DialogInterface.OnShowListener?) {
        mOnShowObservers.takeUnless { it.contains(listener) }?.add(listener ?: return)
    }

    fun addOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        mOnDismissObservers.takeUnless { it.contains(listener) }?.add(listener ?: return)
    }

    fun showPopupWindow() {
        isVisible = true
        mOnShowObservers.forEach { it.onShow(this) }
    }

    override fun cancel() {
        dismiss()
    }

    override fun dismiss() {
        mOnDismissObservers.forEach { it.onDismiss(this) }
        detachFromWindow()
    }

    fun attachToWindow(window: Window): SuperPopupWindow {
        detachFromWindow()
        window.addContentView(this, ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        return this
    }

    private fun detachFromWindow() {
        // 1、清除焦点
        findFocus()?.let {
            it.clearFocus()
            windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.ime())
        }
        // 2、从父容器中移除自己
        (parent as? ViewGroup)?.removeView(this)
    }
}