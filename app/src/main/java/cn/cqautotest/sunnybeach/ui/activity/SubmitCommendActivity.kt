package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.collection.arrayMapOf
import androidx.core.widget.addTextChangedListener
import androidx.viewbinding.ViewBinding
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.SubmitCommendActivityBinding
import cn.cqautotest.sunnybeach.execption.ServiceException
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import cn.cqautotest.sunnybeach.util.simpleToast
import cn.cqautotest.sunnybeach.util.textString
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/19
 * desc   : 评论回复页（发表动态评论/回复动态评论）
 */
class SubmitCommendActivity : AppActivity() {

    private lateinit var mBinding: SubmitCommendActivityBinding
    private val mFishPondViewModel by viewModels<FishPondViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = SubmitCommendActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    override fun initView() {
        val contentContainer = mBinding.windowContainer
        val animation = AlphaAnimation(0.2f, 0.53f)
        animation.duration = 100
        contentContainer.startAnimation(animation)
    }

    override fun initData() {

    }

    /**
     * 获取动态Id
     */
    private fun getMomentId() = intent.getStringExtra(MOMENT_ID)

    /**
     * 获取被评论内容的Id
     */
    private fun getCommentId() = intent.getStringExtra(COMMENT_ID)

    /**
     * 被评论内容的用户Id
     */
    private fun getTargetUserId() = intent.getStringExtra(TARGET_USER_ID)

    override fun initEvent() {
        val keyboardLayout = mBinding.keyboardLayout
        val etInputContent = mBinding.etInputContent
        mBinding.windowContainer.setFixOnClickListener {
            finish()
        }
        mBinding.contentContainer.setFixOnClickListener {
            // Nothing to do.
        }
        etInputContent.setFixOnClickListener {
            keyboardLayout.postDelayed({
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }, 200)
        }
        val rlMenuContainer = mBinding.rlMenuContainer
        keyboardLayout.setKeyboardListener { isActive, keyboardHeight ->
            val height = if (isActive) {
                keyboardHeight
            } else {
                -rlMenuContainer.height
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = height + rlMenuContainer.height + 10.dp
            rlMenuContainer.layoutParams = layoutParams
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
        mBinding.tvSend.setFixOnClickListener {
            val momentId = getMomentId()
            val commentId = getCommentId()
            val targetUserId = getTargetUserId()
            val content = etInputContent.textString
            val momentComment = arrayMapOf("momentId" to momentId, "content" to content)
            // 校验内容是否合法，发布信息
            val inputLength = mBinding.etInputContent.length()
            val textLengthIsOk = inputLength in 1..512
            takeIf { textLengthIsOk.not() }?.let {
                simpleToast("请输入[1, 512)个字符~")
                return@setFixOnClickListener
            }
            mFishPondViewModel.postComment(momentComment).observe(this) {
                it.getOrElse { throwable ->
                    if (throwable is ServiceException) {
                        throwable.message?.let { msg ->
                            LoginActivity.start(this, "", "")
                            simpleToast(msg)
                            return@getOrElse
                        }
                    }
                    simpleToast("评论失败，请稍后重试")
                    return@observe
                }
                setResult(Activity.RESULT_OK)
                finish()
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

        /**
         * 获取发表评论(评论动态)的意图
         */
        fun getCommentIntent(
            context: Context,
            momentId: String,
            commentId: String = "",
            targetUserId: String = ""
        ): Intent {
            return Intent(context, SubmitCommendActivity::class.java).apply {
                putExtra(MOMENT_ID, momentId)
                putExtra(COMMENT_ID, commentId)
                putExtra(TARGET_USER_ID, targetUserId)
            }
        }

        fun start(
            context: Context,
            momentId: String,
            commentId: String = "",
            targetUserId: String = ""
        ) {
            val intent = getCommentIntent(context, momentId, commentId, targetUserId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}