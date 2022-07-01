package cn.cqautotest.sunnybeach.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/24
 * desc   : MaskView 可二次分发触摸事件的 View
 */
class MaskView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var llTopContainer: LinearLayout? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        // Distribute the touch events of the view with the tag mask_view to the view with the tag ll_top_container.
        findViewWithTag<View>("mask_view").setOnTouchListener { _, event -> llTopContainer?.dispatchTouchEvent(event) ?: true }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        llTopContainer = findViewWithTag("ll_top_container")
        require(llTopContainer != null) { "are you ok?　ll_top_container must not be null!" }
    }
}