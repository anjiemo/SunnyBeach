package cn.cqautotest.sunnybeach.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import cn.cqautotest.sunnybeach.action.CommendAction
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.COMMENT_ID
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.IS_REPLY
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.MOMENT_ID
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.TARGET_USER_ID
import cn.cqautotest.sunnybeach.action.CommendAction.Companion.TARGET_USER_NAME
import cn.cqautotest.sunnybeach.execption.NotLoginException
import cn.cqautotest.sunnybeach.ktx.simpleToast
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.FishPondDetailActivity
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import cn.cqautotest.sunnybeach.ui.popup.CommentPopupWindow
import cn.cqautotest.sunnybeach.ui.popup.InputPopup
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/09
 * desc   : 评论回复页（发表动态评论/回复动态评论） Fragment
 */
class SubmitCommentFragment : Fragment(), CommendAction {

    private val mFishPondViewModel by activityViewModels<FishPondViewModel>()
    private val mInputPopup by lazy {
        CommentPopupWindow(requireContext()).apply {
            type = InputPopup.EMOJI_FLAG
            doAfterTextChanged { submitButton.isEnabled = it.isNullOrEmpty().not() }
            // 提交评论
            setOnCommitListener { view, inputContent -> submitComment(view, inputContent) }
            addOnDismissListener {
                resetForm()
            }
        }
    }

    private fun submitComment(view: View, inputContent: String) {
        view.isEnabled = false
        val momentComment = mapOf(
            "momentId" to getMomentId(),
            "content" to inputContent,
            "commentId" to getCommentId(),
            "targetUserId" to getTargetUserId()
        )
        mFishPondViewModel.postComment(momentComment, isReply()).observe(this) { result ->
            view.isEnabled = true
            result.onSuccess {
                simpleToast("评论成功\uD83D\uDE03")
                mInputPopup.dismiss()
                with(requireActivity()) {
                    setResult(Activity.RESULT_OK)
                    if (this is FishPondDetailActivity) {
                        refreshFishPondDetailCommendList()
                    } else {
                        finish()
                    }
                }
            }.onFailure {
                when (it) {
                    is NotLoginException -> {
                        LoginActivity.start(requireContext(), UserManager.getCurrLoginAccount(), UserManager.getCurrLoginAccountPassword())
                        simpleToast(it.message)
                    }
                    else -> simpleToast("评论失败，请稍后重试\uD83D\uDE2D")
                }
            }
        }
    }

    fun show() {
        showCommentPopup(getTargetUserName())
    }

    private fun showCommentPopup(targetUserName: String) {
        mInputPopup.apply {
            // inputHint = "回复 $targetUserName"
            attachToWindow(requireActivity().window)
            showPopupWindow()
        }
    }

    override fun getCommentArgs(): Bundle = requireArguments()

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

        /**
         * 获取 Fragment 的 tag
         */
        fun getFragmentTag(fragment: Fragment) = fragment.hashCode().toString()

        /**
         * activity：依附的 Activity 对象
         * submitCommentFragment：评论 Fragment 对象
         * fragmentTag：Fragment 标签
         * args：Fragment 参数，从 SubmitCommentFragment#getCommentArgs 方法获取
         */
        fun show(
            activity: AppCompatActivity,
            submitCommentFragment: SubmitCommentFragment,
            fragmentTag: String,
            args: Bundle
        ) {
            show(activity.supportFragmentManager, submitCommentFragment, fragmentTag, args)
        }

        /**
         * fragment：依附的 Fragment 对象
         * submitCommentFragment：评论 Fragment 对象
         * fragmentTag：Fragment 标签
         * args：Fragment 参数，从 SubmitCommentFragment#getCommentArgs 方法获取
         */
        fun show(
            fragment: Fragment,
            submitCommentFragment: SubmitCommentFragment,
            fragmentTag: String,
            args: Bundle
        ) {
            show(fragment.parentFragmentManager, submitCommentFragment, fragmentTag, args)
        }

        /**
         * fragmentManager：FragmentManager 对象
         * submitCommentFragment：评论 Fragment 对象
         * fragmentTag：Fragment 标签
         * args：Fragment 参数，从 SubmitCommentFragment#getCommentArgs 方法获取
         */
        fun show(
            fragmentManager: FragmentManager,
            submitCommentFragment: SubmitCommentFragment,
            fragmentTag: String,
            args: Bundle
        ) {
            submitCommentFragment.arguments = args
            fragmentManager.apply {
                val fragment = findFragmentByTag(fragmentTag)
                val ft = beginTransaction()
                takeIf { fragment == null }?.let { ft.add(submitCommentFragment, fragmentTag) }
                ft.show(submitCommentFragment)
                    .runOnCommit { submitCommentFragment.show() }
                    .commit()
            }
        }
    }
}