package cn.cqautotest.sunnybeach.ui.popup

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.animation.Animation
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.SubmitCommendIncludeBinding
import cn.cqautotest.sunnybeach.ktx.simpleToast
import com.bumptech.glide.Glide
import razerdp.basepopup.BasePopupWindow
import razerdp.util.KeyboardUtils
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

class InputPopup(context: Context) : BasePopupWindow(context), KeyboardUtils.OnKeyboardChangeListener {

    private var _binding: SubmitCommendIncludeBinding? = null
    private val mBinding get() = _binding!!
    private var mShowing = true

    init {
        setContentView(R.layout.submit_commend_include)
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        _binding = SubmitCommendIncludeBinding.bind(contentView)
        initEvent()
    }

    private fun initEvent() {
        setOnKeyboardChangeListener(this)
        with(mBinding) {
            etInputContent.setOnClickListener { mBinding.flPanelContainer.isVisible = false }
            ivEmoji.setOnClickListener {
                flPanelContainer.isVisible = mShowing
                val emojiIcon = if (mShowing) R.mipmap.ic_keyboard else R.mipmap.ic_emoji_normal
                Glide.with(it)
                    .load(emojiIcon)
                    .into(mBinding.ivEmoji)
                if (mShowing) {
                    KeyboardUtils.close(it)
                    simpleToast("隐藏键盘")
                } else {
                    KeyboardUtils.open(it)
                    simpleToast("显示键盘")
                }
            }
        }
    }

    override fun onKeyboardChange(rect: Rect, showing: Boolean) {
        val keyboardHeight = rect.height()
        with(mBinding.flPanelContainer) {
            takeIf { showing && height != keyboardHeight }?.let { updateLayoutParams { height = keyboardHeight } }
        }
        mShowing = showing
    }

    override fun onCreateShowAnimation(): Animation = AnimationHelper.asAnimation()
        .withTranslation(TranslationConfig.FROM_BOTTOM)
        .toShow()

    override fun onCreateDismissAnimation(): Animation = AnimationHelper.asAnimation()
        .withTranslation(TranslationConfig.TO_BOTTOM)
        .toDismiss()
}