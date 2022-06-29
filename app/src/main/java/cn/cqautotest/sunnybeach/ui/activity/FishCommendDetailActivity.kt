package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishCommendDetailActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.FishCommendDetailListAdapter
import cn.cqautotest.sunnybeach.ui.fragment.SubmitCommentFragment
import cn.cqautotest.sunnybeach.util.SimpleLinearSpaceItemDecoration
import com.blankj.utilcode.util.TimeUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/18
 * desc   : 摸鱼评论列表页
 */
class FishCommendDetailActivity : AppActivity() {

    private val mBinding: FishCommendDetailActivityBinding by viewBinding()
    private val mFishCommendDetailListAdapter = FishCommendDetailListAdapter()
    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)
    private val mSubmitCommentFragment = SubmitCommentFragment()

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
        val ivAvatar = fishPondDetailComment.ivFishPondAvatar
        val tvNickName = fishPondDetailComment.cbFishPondNickName
        fishPondDetailComment.ivFishPondComment.isVisible = false
        val tvDesc = fishPondDetailComment.tvFishPondDesc
        val tvReply = fishPondDetailComment.tvReplyMsg
        val tvBuildReplyMsgContainer = fishPondDetailComment.tvBuildReplyMsgContainer
        ivAvatar.loadAvatar(item.vip, item.avatar)
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvNickName.text = item.getNickName()
        // 摸鱼详情列表的时间没有精确到秒
        tvDesc.text = "${item.position} · " + TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
        tvDesc.maxLines = Int.MAX_VALUE
        tvReply.setDefaultEmojiParser()
        tvReply.text = item.content
        tvBuildReplyMsgContainer.isVisible = false
        mFishCommendDetailListAdapter.setData(item)
    }

    override fun initEvent() {
        mBinding.titleBar.setOnClickListener {}
        val fishPondDetailComment = mBinding.fishPondDetailComment
        val ivFishPondAvatar = fishPondDetailComment.ivFishPondAvatar
        ivFishPondAvatar.setFixOnClickListener {
            val item: FishPondComment.FishPondCommentItem = getFishPondCommentItem()
            val userId = item.getUserId()
            if (TextUtils.isEmpty(userId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(this, userId)
        }
        mFishCommendDetailListAdapter.setOnCommentClickListener { item, _ ->
            val commendId = item.getCommentId()
            val nickName = item.getNickName()
            val userId = item.getUserId()
            Timber.d("commendId is $commendId nickName is $nickName userId is $userId")
            goToReplyComment(commendId, nickName, userId)
        }
        val clReplyContainer = mBinding.commentContainer.clReplyContainer
        clReplyContainer.setFixOnClickListener {
            val item = getFishPondCommentItem()
            val commendId = item.getCommentId()
            val nickName = item.getNickName()
            val userId = item.getUserId()
            goToReplyComment(commendId, nickName, userId)
        }
        mBinding.commentContainer.tvFishPondSubmitComment.setFixOnClickListener {
            val item = getFishPondCommentItem()
            val commendId = item.getCommentId()
            val nickName = item.getNickName()
            val userId = item.getUserId()
            goToReplyComment(commendId, nickName, userId)
        }
    }

    private fun getMomentId(): String = intent.getStringExtra(IntentKey.ID).orEmpty()

    private fun getFishPondCommentItem(): FishPondComment.FishPondCommentItem =
        fromJson(intent.getStringExtra(IntentKey.OTHER))

    /**
     * 去回复评论
     */
    private fun goToReplyComment(commentId: String, targetUserName: String, targetUserId: String) {
        takeIfLogin { showCommentPopupFragment(targetUserName, commentId, targetUserId) }
    }

    private fun showCommentPopupFragment(targetUserName: String, commentId: String, targetUserId: String) {
        val args = SubmitCommentFragment.getCommentArgs(targetUserName, getMomentId(), commentId, targetUserId, true)
        val fragmentTag = SubmitCommentFragment.getFragmentTag(mSubmitCommentFragment)
        SubmitCommentFragment.show(this, mSubmitCommentFragment, fragmentTag, args)
    }

    companion object {

        @Log
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