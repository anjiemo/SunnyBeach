package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.GestureDetector
import android.view.ViewConfiguration
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishCommendDetailActivityBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.other.KeyboardWatcher
import cn.cqautotest.sunnybeach.ui.adapter.FishCommendDetailListAdapter
import cn.cqautotest.sunnybeach.ui.fragment.SubmitCommentFragment
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.KeyboardViewModel
import com.bumptech.glide.Glide
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/18
 * desc   : 摸鱼评论列表页
 */
class FishCommendDetailActivity : AppActivity(), SimpleGesture.OnSlideListener,
    KeyboardWatcher.SoftKeyboardStateListener {

    private val mBinding: FishCommendDetailActivityBinding by viewBinding()
    private val mKeyboardViewModel by viewModels<KeyboardViewModel>()
    private val mFishCommendDetailListAdapter = FishCommendDetailListAdapter()

    override fun getLayoutId(): Int = R.layout.fish_commend_detail_activity

    override fun initView() {
        mBinding.rvFishCommendDetailList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishCommendDetailListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
        postDelayed({
            KeyboardWatcher.with(this)
                .setListener(this)
        }, 500)
    }

    override fun onSoftKeyboardOpened(keyboardHeight: Int) {
        mKeyboardViewModel.showKeyboard()
        mKeyboardViewModel.setKeyboardHeight(keyboardHeight)
    }

    override fun onSoftKeyboardClosed() {
        mKeyboardViewModel.hideKeyboard()
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val item: FishPondComment.FishPondCommentItem = getFishPondCommentItem()
        val tvReplyAndGreat = mBinding.tvReplyAndGreat
        tvReplyAndGreat.text = "${item.subComments.size} 回复  |  ${item.thumbUp} 赞"
        val fishPondDetailComment = mBinding.fishPondDetailComment
        val flAvatarContainer = fishPondDetailComment.flAvatarContainer
        val ivAvatar = fishPondDetailComment.ivFishPondAvatar
        val tvNickName = fishPondDetailComment.cbFishPondNickName
        fishPondDetailComment.ivFishPondComment.isVisible = false
        val tvDesc = fishPondDetailComment.tvFishPondDesc
        val tvReply = fishPondDetailComment.tvReplyMsg
        val tvBuildReplyMsgContainer = fishPondDetailComment.tvBuildReplyMsgContainer
        flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(this)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvNickName.text = item.getNickName()
        // 摸鱼详情列表的时间没有精确到秒
        val time = "${item.createTime}:00"
        tvDesc.text = "${item.position} · " + DateHelper.getFriendlyTimeSpanByNow(time)
        tvDesc.maxLines = Int.MAX_VALUE
        tvReply.setDefaultEmojiParser()
        tvReply.text = item.content
        tvBuildReplyMsgContainer.isVisible = false
        mFishCommendDetailListAdapter.setData(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initEvent() {
        val fishPondDetailComment = mBinding.fishPondDetailComment
        val flAvatarContainer = fishPondDetailComment.flAvatarContainer
        flAvatarContainer.setFixOnClickListener {
            val item: FishPondComment.FishPondCommentItem = getFishPondCommentItem()
            val userId = item.getUserId()
            if (TextUtils.isEmpty(userId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(context, userId)
        }
        mFishCommendDetailListAdapter.setOnCommentClickListener { item, _ ->
            val commendId = item.getCommentId()
            val nickName = item.getNickName()
            val userId = item.getUserId()
            Timber.d("commendId is $commendId nickName is $nickName userId is $userId")
            goToReplyComment(commendId, nickName, userId)
        }
        val clReplyContainer = mBinding.commentContainer.clReplyContainer
        // 如果要使用 GestureDetector 手势检测器，则必须禁用点击事件，否则无法检测手势
        clReplyContainer.setOnClickListener(null)
        val minDistance = ViewConfiguration.get(this).scaledTouchSlop
        val sg = SimpleGesture(minDistance, this)
        val gestureDetector = GestureDetector(this, sg)
        clReplyContainer.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        mBinding.commentContainer.tvFishPondSubmitComment.setFixOnClickListener {
            val item = getFishPondCommentItem()
            val commendId = item.getCommentId()
            val nickName = item.getNickName()
            val userId = item.getUserId()
            goToReplyComment(commendId, nickName, userId)
        }
    }

    override fun onSwipeUp() {
        val item = getFishPondCommentItem()
        val commendId = item.getCommentId()
        val nickName = item.getNickName()
        val userId = item.getUserId()
        goToReplyComment(commendId, nickName, userId)
    }

    private fun getMomentId(): String = intent.getStringExtra(IntentKey.ID) ?: ""

    private fun getFishPondCommentItem(): FishPondComment.FishPondCommentItem =
        fromJson(intent.getStringExtra(IntentKey.OTHER))

    /**
     * 去回复评论
     */
    private fun goToReplyComment(commentId: String, targetUserName: String, targetUserId: String) {
        takeIfLogin {
            safeShowFragment(targetUserName, commentId, targetUserId)
        }
    }

    private fun safeShowFragment(targetUserName: String, commentId: String, targetUserId: String) {
        val args = SubmitCommentFragment.getCommentArgs(
            targetUserName,
            getMomentId(),
            commentId,
            targetUserId,
            true
        )
        val dialogFragment = SubmitCommentFragment()
        dialogFragment.arguments = args
        dialogFragment.show(supportFragmentManager, dialogFragment.tag)
    }

    companion object {
        @DebugLog
        @JvmStatic
        fun getIntent(
            context: Context,
            momentId: String,
            fishPondCommentItem: FishPondComment.FishPondCommentItem
        ): Intent {
            val intent = Intent(context, FishCommendDetailActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra(IntentKey.ID, momentId)
            intent.putExtra(IntentKey.OTHER, fishPondCommentItem.toJson())
            return intent
        }
    }
}