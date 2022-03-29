package cn.cqautotest.sunnybeach.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.action.CommendAction
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.COMMENT_ID
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.IS_REPLY
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.MOMENT_ID
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.TARGET_USER_ID
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.TARGET_USER_NAME
import cn.cqautotest.sunnybeach.action.Init
import cn.cqautotest.sunnybeach.databinding.SubmitCommendIncludeBinding
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.KeyboardViewModel
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hjq.base.BottomSheetDialog
import com.hjq.base.action.KeyboardAction

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/09
 * desc   : 评论回复页（发表动态评论/回复动态评论） Fragment
 */
class SubmitCommentFragment : BottomSheetDialogFragment(), Init, KeyboardAction, CommendAction {

    private var _binding: SubmitCommendIncludeBinding? = null
    private val mBinding: SubmitCommendIncludeBinding get() = _binding!!
    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mKeyboardViewModel by activityViewModels<KeyboardViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())
        _binding = SubmitCommendIncludeBinding.inflate(layoutInflater)
        dialog.contentView = mBinding.root
        onCreateDialogView()
        return dialog
    }

    override fun onStart() {
        super.onStart()
        // 使用 BottomSheetDialogFragment 删除底表中的暗淡背景：https://stackoverflow.com/questions/58251660/remove-dim-background-in-bottomsheet-using-bottomsheetdialogfragment
        // dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun onCreateDialogView() {
        initView()
        initData()
        initEvent()
        initObserver()
    }

    private fun initView() {
        val etInputContent = mBinding.etInputContent
        etInputContent.setRoundRectBg(Color.parseColor("#F7F7F7"), 4.dp)
        etInputContent.setDefaultEmojiParser()
        etInputContent.post {
            showKeyboard(etInputContent)
        }
        mBinding.ivImage.isVisible = false
    }

    private fun initData() {
        mBinding.etInputContent.hint = "回复 ${getTargetUserName()}"
    }

    override fun getCommentArgs(): Bundle = requireArguments()

    override fun initEvent() {
        val etInputContent = mBinding.etInputContent
        mBinding.ivEmoji.setOnClickListener {
            // 如果键盘已经显示就隐藏，如果键盘已经隐藏就显示
            toggleSoftInput(etInputContent)
        }
        mBinding.rvEmojiList.setOnEmojiClickListener { emoji, _ ->
            val cursor = etInputContent.selectionStart
            etInputContent.text.insert(cursor, emoji)
        }
        val normalColor = Color.parseColor("#CBD0D3")
        val overflowColor = Color.RED
        mBinding.etInputContent.addTextChangedListener {
            // 最大字符输入长度
            val maxInputTextLength = 512
            // 最小字符输入长度
            val minInputTextLength = 5
            val inputLength = mBinding.etInputContent.length()
            // 判断输入的字符长度是否溢出
            val isOverflow = (maxInputTextLength - inputLength) < 0
            // 如果输入的字符长度溢出了，则为 -number 样式，否则为 number / maxInputTextLength 的样式
            val inputLengthTips =
                if (inputLength < minInputTextLength || isOverflow) (maxInputTextLength - inputLength).toString()
                else "${inputLength}/$maxInputTextLength"
            mBinding.tvInputLength.text = inputLengthTips
            // 判断输入的字符串长度是否超过最大长度
            mBinding.tvInputLength.setTextColor(if (isOverflow) overflowColor else normalColor)
        }
        mBinding.tvSend.setFixOnClickListener { view ->
            view.isEnabled = false
            val momentId = getMomentId()
            val commentId = getCommentId()
            val targetUserId = getTargetUserId()
            val isReply = isReply()
            val content = etInputContent.textString
            val momentComment = mapOf(
                "momentId" to momentId,
                "content" to content,
                "commentId" to commentId,
                "targetUserId" to targetUserId
            )
            // 校验内容是否合法，发布信息
            val inputLength = mBinding.etInputContent.length()
            val textLengthIsOk = inputLength in 1..512
            takeIf { textLengthIsOk.not() }?.let {
                simpleToast("请输入[1, 512)个字符~")
                view.isEnabled = true
                return@setFixOnClickListener
            }
            mFishPondViewModel.postComment(momentComment, isReply).observe(this) {
                view.isEnabled = true
                it.getOrElse { throwable ->
                    takeIf { throwable is ServiceException }?.let {
                        throwable.message?.let { msg ->
                            LoginActivity.start(requireContext(), "", "")
                            simpleToast(msg)
                            return@getOrElse
                        }
                    }
                    simpleToast("评论失败，请稍后重试\uD83D\uDE2D")
                    return@observe
                }
                simpleToast("评论成功\uD83D\uDE03")
                dismissAllowingStateLoss()
                val activity = requireActivity()
                activity.setResult(Activity.RESULT_OK)
                if (activity is FishPondDetailActivity) {
                    activity.refreshFishPondDetailCommendList()
                } else {
                    activity.finish()
                }
            }
        }
    }

    override fun initObserver() {
        val etInputContent = mBinding.etInputContent
        val flPanelContainer = mBinding.flPanelContainer
        val rvEmojiList = mBinding.rvEmojiList
        val keyboardHeight = mKeyboardViewModel.keyboardHeightLiveData.value ?: 310.dp
        flPanelContainer.updateLayoutParams {
            height = keyboardHeight
        }
        mKeyboardViewModel.keyboardStateLiveData.observe(this) { keyboardIsShowing ->
            rvEmojiList.isVisible = !keyboardIsShowing
            val emojiIcon = if (keyboardIsShowing) {
                R.mipmap.ic_emoji_normal
            } else {
                R.mipmap.ic_keyboard
            }
            Glide.with(this)
                .load(emojiIcon)
                .into(mBinding.ivEmoji)
        }
        mKeyboardViewModel.keyboardStateLiveData.observe(this) { show ->
            dialog?.window?.decorView?.let {
                if (show) {
                    showKeyboard(etInputContent)
                } else {
                    hideKeyboard(etInputContent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        /**
         * 获取发表评论(评论动态)的 Bundle 参数
         */
        fun getCommentArgs(
            targetUserName: String = "",
            momentId: String,
            commentId: String = "",
            targetUserId: String = "",
            isReply: Boolean
        ): Bundle {
            return Bundle().apply {
                putString(TARGET_USER_NAME, targetUserName)
                putString(MOMENT_ID, momentId)
                putString(COMMENT_ID, commentId)
                putString(TARGET_USER_ID, targetUserId)
                putBoolean(IS_REPLY, isReply)
            }
        }
    }
}