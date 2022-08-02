package cn.cqautotest.sunnybeach.other

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber
import kotlin.math.max

class DeferringInsetsAnimationCallback(private val view: View) : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

    var keepKeyboardLock = false

    /**
     * 键盘滑动中的回调
     */
    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: MutableList<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        val insetsByIme = insets.getInsets(WindowInsetsCompat.Type.ime())
        val insetsByStatusBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val imeHeight = insetsByIme.bottom - insetsByIme.top
        val statusBarHeight = insetsByStatusBar.bottom - insetsByStatusBar.top

        val offset = max(imeHeight - statusBarHeight, 0)

        val keyboardShowing = insets.isVisible(WindowInsetsCompat.Type.ime())
        Timber.d("onProgress：===> keyboardShowing is $keyboardShowing")

        if (keepKeyboardLock.not()) {
            view.layoutParams = LinearLayout.LayoutParams(view.layoutParams).apply {
                Timber.d("onProgress：===> imeHeight is $imeHeight")
                Timber.d("onProgress：===> statusBarHeight is $statusBarHeight")
                Timber.d("onProgress：===> offset is $offset")
                bottomMargin = offset
            }
        }
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        super.onEnd(animation)
        view.post { keepKeyboardLock = false }
        // Copy from：https://github.com/android/user-interface-samples/blob/master/WindowInsetsAnimation/app/src/main/java/com/google/android/samples/insetsanimation/ControlFocusInsetsAnimationCallback.kt
        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            // The animation has now finished, so we can check the view's focus state.
            // We post the check because the rootWindowInsets has not yet been updated, but will
            // be in the next message traversal
            view.post {
                checkFocus()
            }
        }
        Timber.d("onEnd：===> view y is ${view.y}")
    }

    private fun checkFocus() {
        val imeVisible = ViewCompat.getRootWindowInsets(view)
            ?.isVisible(WindowInsetsCompat.Type.ime()) == true
        if (imeVisible && view.rootView.findFocus() == null) {
            // If the IME will be visible, and there is not a currently focused view in
            // the hierarchy, request focus on our view
            view.requestFocus()
        } else if (!imeVisible && view.isFocused) {
            // If the IME will not be visible and our view is currently focused, clear the focus
            view.clearFocus()
        }
    }
}