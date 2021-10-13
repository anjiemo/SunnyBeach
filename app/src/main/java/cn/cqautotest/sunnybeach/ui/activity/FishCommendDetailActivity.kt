package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishCommendDetailActivityBinding
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.FishCommendDetailListAdapter
import cn.cqautotest.sunnybeach.util.*
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/18
 * desc   : 摸鱼评论列表页
 */
class FishCommendDetailActivity : AppActivity(), SimpleGesture.OnSlideListener {

    private val mBinding: FishCommendDetailActivityBinding by viewBinding()
    private val mFishCommendDetailListAdapter = FishCommendDetailListAdapter()

    override fun getLayoutId(): Int = R.layout.fish_commend_detail_activity

    override fun initView() {
        mBinding.rvFishCommendDetailList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishCommendDetailListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val item: FishPondComment.FishPondCommentItem = getFishPondCommentItem()
        val tvReplyAndGreat = mBinding.tvReplyAndGreat
        tvReplyAndGreat.text = "${item.subComments.size} 回复  |  ${item.thumbUp} 赞"
        val fishPondDetailComment = mBinding.fishPondDetailComment
        val flAvatarContainer = fishPondDetailComment.flAvatarContainer
        val ivAvatar = fishPondDetailComment.ivFishPondAvatar
        val tvNickname = fishPondDetailComment.cbFishPondNickName
        fishPondDetailComment.ivFishPondComment.visibility = View.GONE
        val ivPondComment = fishPondDetailComment.ivFishPondComment
        val tvDesc = fishPondDetailComment.tvFishPondDesc
        val tvReply = fishPondDetailComment.tvReplyMsg
        val tvBuildReplyMsgContainer = fishPondDetailComment.tvBuildReplyMsgContainer
        flAvatarContainer.background = if (item.vip) ContextCompat.getDrawable(
            context,
            R.drawable.avatar_circle_vip_ic
        ) else null
        Glide.with(this)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        tvNickname.setTextColor(
            ContextCompat.getColor(
                context, if (item.vip) {
                    R.color.pink
                } else {
                    R.color.black
                }
            )
        )
        tvNickname.text = item.getNickName()
        ivPondComment.setFixOnClickListener {
            goToPostComment(item.getNickName())
        }
        // 摸鱼详情列表的时间没有精确到秒
        tvDesc.text = "${item.position} · " +
                DateHelper.transform2FriendlyTimeSpanByNow("${item.createTime}:00")
        tvReply.text = item.content
        tvBuildReplyMsgContainer.visibility = View.GONE
        mFishCommendDetailListAdapter.setData(item)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initEvent() {
        mFishCommendDetailListAdapter.setOnCommentClickListener { item, _ ->
            goToPostComment(item.getCommentId(), item.getNickName(), item.getUserId())
        }
        val clReplyContainer = mBinding.commentContainer.clReplyContainer
        // 如果要使用 GestureDetector 手势检测器，则必须禁用点击事件，否则无法检测手势
        clReplyContainer.setOnClickListener(null)
        val minDistance = ViewConfiguration.get(this).scaledTouchSlop
        val sg = SimpleGesture(minDistance, this)
        val gestureDetector = GestureDetector(this, sg)
        clReplyContainer.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
        mBinding.commentContainer.tvFishPondSubmitComment.setFixOnClickListener {
            goToPostComment(getFishPondCommentItem().getNickName())
        }
    }

    override fun onSwipeUp() {
        goToPostComment(getFishPondCommentItem().getNickName())
    }

    private fun getMomentId(): String = intent.getStringExtra(IntentKey.ID) ?: ""

    private fun getFishPondCommentItem(): FishPondComment.FishPondCommentItem =
        fromJson(intent.getStringExtra(IntentKey.OTHER))

    private fun goToPostComment(targetUserName: String) {
        goToPostComment("", targetUserName, "", false)
    }

    /**
     * 去发表评论（isReply：false）/回复评论（isReply：true）
     */
    private fun goToPostComment(
        commentId: String,
        targetUserName: String,
        targetUserId: String,
        isReply: Boolean = true
    ) {
        val intent = SubmitCommendActivity.getCommentIntent(
            this,
            targetUserName,
            getMomentId(),
            commentId,
            targetUserId,
            isReply
        )
        startActivity(intent)
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