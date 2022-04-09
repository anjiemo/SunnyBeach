package cn.cqautotest.sunnybeach.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SubmitCommendActivityBinding
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.manager.ActivityManager
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import cn.cqautotest.sunnybeach.util.simpleToast
import cn.cqautotest.sunnybeach.util.textString
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.blankj.utilcode.util.KeyboardUtils
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/19
 * desc   : 评论回复页（发表动态评论/回复动态评论）
 */
class SubmitCommendActivity : AppActivity() {

    private val mBinding: SubmitCommendActivityBinding by viewBinding()
    private val mFishPondViewModel by viewModels<FishPondViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        decorView.setPadding(0)
        window.attributes.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            gravity = Gravity.BOTTOM
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun getLayoutId(): Int = R.layout.submit_commend_activity

    override fun initView() {

    }

    override fun initData() {
        mBinding.etInputContent.hint = "回复 ${getTargetUserName()}"
    }

    /**
     * 获取被评论的用户名称
     */
    private fun getTargetUserName() = intent.getStringExtra(TARGET_USER_NAME) ?: ""

    /**
     * 获取动态Id
     */
    private fun getMomentId() = intent.getStringExtra(MOMENT_ID) ?: ""

    /**
     * 获取被评论内容的Id
     */
    private fun getCommentId() = intent.getStringExtra(COMMENT_ID) ?: ""

    /**
     * 被评论内容的用户Id
     */
    private fun getTargetUserId() = intent.getStringExtra(TARGET_USER_ID) ?: ""

    /**
     * 是否为评论回复
     */
    private fun isReply() = intent.getBooleanExtra(IS_REPLY, false)

    override fun initEvent() {
        postDelayed({
            showKeyboard(mBinding.etInputContent)
        }, 200)
        // val keyboardLayout = mBinding.keyboardLayout
        val etInputContent = mBinding.etInputContent
        mBinding.windowContainer.setFixOnClickListener {
            finish()
        }
        mBinding.ivEmoji.setFixOnClickListener {
            // 选择表情，弹出表情选择列表
            val keyboardIsShowing = KeyboardUtils.isSoftInputVisible(this)
            if (keyboardIsShowing) {
                postDelayed({
                    mBinding.rvEmojiList.isVisible = true
                    mBinding.rvEmojiList.layoutParams =
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 310.dp)
                }, 200)
                hideKeyboard()
            } else {
                mBinding.rvEmojiList.isVisible = false
                showKeyboard(mBinding.etInputContent)
            }
            val emojiIcon = if (keyboardIsShowing) {
                R.mipmap.ic_keyboard
            } else {
                R.mipmap.ic_emoji_normal
            }
            Glide.with(this)
                .load(emojiIcon)
                .into(mBinding.ivEmoji)
        }
        mBinding.rvEmojiList.setOnEmojiClickListener { emoji, _ ->
            val cursor = etInputContent.selectionStart
            etInputContent.text.insert(cursor, emoji)
        }
        etInputContent.setFixOnClickListener {
            // keyboardLayout.postDelayed({
            //     window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            // }, 200)
        }
        val rlMenuContainer = mBinding.rlMenuContainer
        // keyboardLayout.setKeyboardListener { isActive, keyboardHeight ->
        //     val height = if (isActive) {
        //         keyboardHeight
        //     } else {
        //         -rlMenuContainer.height
        //     }
        //     val layoutParams = LinearLayout.LayoutParams(
        //         LinearLayout.LayoutParams.MATCH_PARENT,
        //         LinearLayout.LayoutParams.WRAP_CONTENT
        //     )
        //     layoutParams.bottomMargin = height + rlMenuContainer.height + 10.dp
        //     rlMenuContainer.layoutParams = layoutParams
        // }
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
        mBinding.tvSend.setFixOnClickListener { v ->
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
                return@setFixOnClickListener
            }
            takeIf { v.isEnabled.not() }?.let {
                return@setFixOnClickListener
            }
            v.isEnabled = false
            mFishPondViewModel.postComment(momentComment, isReply).observe(this) {
                v.isEnabled = true
                it.getOrElse { throwable ->
                    takeIf { throwable is ServiceException }?.let {
                        throwable.message?.let { msg ->
                            LoginActivity.start(this, "", "")
                            simpleToast(msg)
                            return@getOrElse
                        }
                    }
                    simpleToast("评论失败，请稍后重试")
                    return@observe
                }
                val am = ActivityManager.getInstance()
                am.finishActivity(javaClass)
                am.finishActivity(FishCommendDetailActivity::class.java)
                am.finishActivity(FishPondDetailActivity::class.java)
                FishPondDetailActivity.start(this, momentId)
            }
        }
    }

    companion object {

        // 动态Id
        private const val MOMENT_ID = "moment_id"

        // 被评论内容的Id
        private const val COMMENT_ID = "comment_id"

        // 被评论内容的用户Id
        private const val TARGET_USER_ID = "target_user_id"

        // 被评论内容的用户名称
        private const val TARGET_USER_NAME = "target_user_name"

        // 是否为评论回复
        private const val IS_REPLY = "is_reply"

        /**
         * 获取发表评论(评论动态)的意图
         */
        @DebugLog
        fun getCommentIntent(
            context: Context,
            targetUserName: String = "",
            momentId: String,
            commentId: String = "",
            targetUserId: String = "",
            isReply: Boolean
        ): Intent {
            return Intent(context, SubmitCommendActivity::class.java).apply {
                putExtra(TARGET_USER_NAME, targetUserName)
                putExtra(MOMENT_ID, momentId)
                putExtra(COMMENT_ID, commentId)
                putExtra(TARGET_USER_ID, targetUserId)
                putExtra(IS_REPLY, isReply)
            }
        }
    }
}