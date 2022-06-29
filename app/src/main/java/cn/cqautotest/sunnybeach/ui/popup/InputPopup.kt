package cn.cqautotest.sunnybeach.ui.popup

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.SubmitCommendIncludeBinding
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.textString
import com.bumptech.glide.Glide
import razerdp.basepopup.BasePopupWindow
import razerdp.util.KeyboardUtils
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/09
 * desc   : 评论回复组件（发表动态评论/回复动态评论） ，支持 emoji、选择图片
 */
class InputPopup(context: Context) : BasePopupWindow(context), KeyboardUtils.OnKeyboardChangeListener {

    private var _binding: SubmitCommendIncludeBinding? = null
    private val mBinding get() = _binding!!
    private var mShowing = true
    private val defaultFlag = EMOJI_FLAG and IMAGE_FLAG
    var inputHint = ""
        set(value) {
            field = value
            mBinding.etInputContent.hint = value
        }
    var type = defaultFlag
        set(value) {
            field = value
            updateMenuItem()
        }
    val submitButton get() = mBinding.tvSend
    private var onCommitListener: OnCommitListener? = null

    init {
        setContentView(R.layout.submit_commend_include)
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        _binding = SubmitCommendIncludeBinding.bind(contentView)
        initView()
        initEvent()
    }

    private fun initView() {
        mBinding.etInputContent.setDefaultEmojiParser()
        updateMenuItem()
    }

    fun defaultConfig() {
        val keyboardFlag = FLAG_KEYBOARD_ALIGN_TO_ROOT or FLAG_KEYBOARD_ANIMATE_ALIGN
        setKeyboardAdaptive(true)
        setAutoShowKeyboard(findViewById(R.id.et_input_content), true)
        setKeyboardGravity(Gravity.BOTTOM)
        setKeyboardAdaptionMode(findViewById(R.id.et_input_content), keyboardFlag)
    }

    private fun updateMenuItem() {
        with(mBinding) {
            when (type) {
                NONE -> {
                    ivEmoji.isVisible = false
                    ivImage.isVisible = false
                }
                EMOJI_FLAG -> {
                    ivEmoji.isVisible = true
                    ivImage.isVisible = false
                }
                IMAGE_FLAG -> {
                    ivEmoji.isVisible = false
                    ivImage.isVisible = true
                }
                EMOJI_FLAG and IMAGE_FLAG -> {
                    ivEmoji.isVisible = true
                    ivImage.isVisible = true
                }
            }
        }
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
                } else {
                    KeyboardUtils.open(it)
                }
            }
            tvSend.setFixOnClickListener { onCommitListener?.onSubmit(it, etInputContent.textString) }
            mBinding.rvEmojiList.setOnEmojiClickListener { emoji, _ ->
                val cursor = etInputContent.selectionStart
                etInputContent.text?.insert(cursor, emoji)
            }
        }
    }

    fun doAfterTextChanged(action: (text: Editable?) -> Unit) {
        with(mBinding) {
            etInputContent.doAfterTextChanged {
                tvInputLength.text = (it?.length ?: 0).toString()
                action.invoke(it)
            }
        }
    }

    fun setOnCommitListener(listener: OnCommitListener?) {
        onCommitListener = listener
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

    fun interface OnCommitListener {
        fun onSubmit(view: View, content: String)
    }

    fun interface OnMenuItemClickListener {
        fun onItemClick(menuType: Type)
    }

    enum class Type {
        // 表情
        EMOJI,

        // 图片
        IMAGE,

        // 链接
        LINK
    }

    companion object {

        const val NONE = -1
        const val EMOJI_FLAG = 1
        const val IMAGE_FLAG = 2
    }
}