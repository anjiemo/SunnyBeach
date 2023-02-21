package cn.cqautotest.sunnybeach.ui.popup

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.view.*
import androidx.core.widget.doAfterTextChanged
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.SubmitCommendIncludeBinding
import cn.cqautotest.sunnybeach.ktx.clearText
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.textString
import com.bumptech.glide.Glide
import com.dylanc.longan.rootWindowInsetsCompat
import com.dylanc.longan.windowInsetsControllerCompat
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2023/02/18
 * desc   : 评论 PopupWindow
 */
class CommentPopupWindow(context: Context, attrs: AttributeSet? = null) : SuperPopupWindow(context, attrs) {

    private var _binding: SubmitCommendIncludeBinding? = null
    private val mBinding get() = _binding!!
    private val mShowing: Boolean
        get() = rootWindowInsetsCompat?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    private val defaultFlag = InputPopup.EMOJI_FLAG and InputPopup.IMAGE_FLAG
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
    private var onCommitListener: InputPopup.OnCommitListener? = null

    override fun onCreate() {
        setContentView(R.layout.submit_commend_include)
    }

    override fun onViewCreated() {
        _binding = SubmitCommendIncludeBinding.bind(contentView)
    }

    override fun initView() {
        mBinding.etInputContent.setDefaultEmojiParser()
        mBinding.etInputContent.requestFocus()
        mBinding.etInputContent.performClick()
        updateMenuItem()
        post {
            rootWindowInsetsCompat?.let {
                val imeInsets = it.getInsets(WindowInsetsCompat.Type.ime())
                val imeHeight = imeInsets.bottom - imeInsets.top
                mBinding.rvEmojiList.updateLayoutParams {
                    height = imeHeight
                }
            }
        }
    }

    private fun updateEmojiIcon() {
        val emojiIcon = if (mShowing.not()) R.mipmap.ic_emoji_normal else R.mipmap.ic_keyboard
        loadEmojiIcon(emojiIcon)
    }

    private fun updateMenuItem() {
        with(mBinding) {
            when (type) {
                InputPopup.NONE -> {
                    ivEmoji.isVisible = false
                    ivImage.isVisible = false
                }
                InputPopup.EMOJI_FLAG -> {
                    ivEmoji.isVisible = true
                    ivImage.isVisible = false
                }
                InputPopup.IMAGE_FLAG -> {
                    ivEmoji.isVisible = false
                    ivImage.isVisible = true
                }
                InputPopup.EMOJI_FLAG and InputPopup.IMAGE_FLAG -> {
                    ivEmoji.isVisible = true
                    ivImage.isVisible = true
                }
            }
        }
    }

    override fun initEvent() {
        setWindowInsetsAnimationCallback()
        mBinding.apply {
            viewMask.setOnClickListener { dismiss() }
            etInputContent.setOnClickListener {
                if (!mShowing) {
                    post { setWindowInsetsAnimationCallback() }
                    mBinding.etInputContent.requestFocus()
                    windowInsetsControllerCompat?.show(WindowInsetsCompat.Type.ime())
                }
            }
            ivEmoji.setFixOnClickListener(delayTime = 300) {
                if (mShowing) {
                    clearWindowInsetsAnimationCallback()
                    windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.ime())
                } else {
                    post { setWindowInsetsAnimationCallback() }
                    mBinding.etInputContent.requestFocus()
                    windowInsetsControllerCompat?.show(WindowInsetsCompat.Type.ime())
                }
            }
            tvSend.setFixOnClickListener { onCommitListener?.onSubmit(it, etInputContent.textString) }
            rvEmojiList.setOnEmojiClickListener { emoji, _ ->
                val cursor = etInputContent.selectionStart
                etInputContent.text?.insert(cursor, emoji)
            }
        }
    }

    private fun setWindowInsetsAnimationCallback() {
        object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

            override fun onProgress(
                insets: WindowInsetsCompat,
                runningAnimations: MutableList<WindowInsetsAnimationCompat>
            ): WindowInsetsCompat {
                val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                val imeHeight = imeInsets.bottom - imeInsets.top
                val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                val navigationBarsHeight = navigationBarsInsets.bottom - navigationBarsInsets.top
                Timber.d("onProgress：===> imeHeight is $imeHeight navigationBarsHeight is $navigationBarsHeight")
                mBinding.rvEmojiList.updateLayoutParams {
                    height = if (imeHeight - navigationBarsHeight <= 0) 0 else imeHeight - navigationBarsHeight
                }
                return insets
            }
        }.also { ViewCompat.setWindowInsetsAnimationCallback(this, it) }
    }

    private fun WindowInsetsCompat?.getKeyboardsHeight(): Int {
        this ?: return 0
        val imeWindowInsets = getInsets(WindowInsetsCompat.Type.ime())
        return imeWindowInsets.bottom - imeWindowInsets.top
    }

    private fun clearWindowInsetsAnimationCallback() {
        ViewCompat.setWindowInsetsAnimationCallback(this, null)
    }

    fun resetForm() {
        mBinding.etInputContent.clearText()
    }

    private fun loadEmojiIcon(@DrawableRes emojiIcon: Int) {
        val ivEmoji = mBinding.ivEmoji
        Glide.with(ivEmoji)
            .load(emojiIcon)
            .into(ivEmoji)
    }

    fun doAfterTextChanged(action: (text: Editable?) -> Unit) {
        with(mBinding) {
            etInputContent.doAfterTextChanged {
                tvInputLength.text = (it?.length ?: 0).toString()
                action.invoke(it)
            }
        }
    }

    fun setOnCommitListener(listener: InputPopup.OnCommitListener?) {
        onCommitListener = listener
    }

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