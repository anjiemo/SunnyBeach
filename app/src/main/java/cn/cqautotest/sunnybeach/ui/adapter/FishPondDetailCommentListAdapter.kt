package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FishPondDetailCommendListBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 摸鱼话题评论列表适配器
 */
class FishPondDetailCommentListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<FishPondComment.FishPondCommentItem, FishDetailCommendListViewHolder>(
        FishCommendDiffCallback()
    ) {

    class FishCommendDiffCallback : DiffUtil.ItemCallback<FishPondComment.FishPondCommentItem>() {
        override fun areItemsTheSame(
            oldItem: FishPondComment.FishPondCommentItem,
            newItem: FishPondComment.FishPondCommentItem
        ): Boolean {
            return oldItem.getId() == newItem.getId()
        }

        override fun areContentsTheSame(
            oldItem: FishPondComment.FishPondCommentItem,
            newItem: FishPondComment.FishPondCommentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var mItemClickListener: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit =
        { _, _ -> }
    private var mViewMoreClickListener: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit =
        { _, _ -> }
    private var mCommentClickListener: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit =
        { _, _ -> }

    fun setOnItemClickListener(block: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit) {
        mItemClickListener = block
    }

    fun setOnVewMoreClickListener(block: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit) {
        mViewMoreClickListener = block
    }

    fun setOnCommentClickListener(block: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit) {
        mCommentClickListener = block
    }

    override fun onViewAttachedToWindow(holder: FishDetailCommendListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FishDetailCommendListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val flAvatarContainer = binding.flAvatarContainer
        val ivAvatar = binding.ivFishPondAvatar
        val tvNickName = binding.cbFishPondNickName
        val ivPondComment = binding.ivFishPondComment
        val tvDesc = binding.tvFishPondDesc
        val tvReply = binding.tvReplyMsg
        val tvBuildReplyMsgContainer = binding.tvBuildReplyMsgContainer
        val tvChildReplyMsg = binding.tvChildReplyMsg
        val tvChildReplyMsg1 = binding.tvChildReplyMsg1
        val tvChildReplyMsgAll = binding.tvChildReplyMsgAll
        val context = itemView.context
        val userId = item.getUserId()
        flAvatarContainer.setFixOnClickListener {
            if (TextUtils.isEmpty(userId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(context, userId)
        }
        flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(holder.itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        ivPondComment.setFixOnClickListener {
            mCommentClickListener.invoke(item, position)
        }
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvNickName.text = item.getNickName()
        // 摸鱼详情列表的时间没有精确到秒
        tvDesc.text = "${item.position} · " +
                DateHelper.getFriendlyTimeSpanByNow("${item.createTime}:00")
        tvReply.setDefaultEmojiParser()
        tvReply.text = item.content
        val subComments = item.subComments
        val buildHeight = subComments.size
        tvBuildReplyMsgContainer.isVisible = subComments.isNotEmpty()
        tvChildReplyMsg.isVisible = false
        subComments.getOrNull(0)?.let {
            tvChildReplyMsg1.setDefaultEmojiParser()
            tvChildReplyMsg.text = getBeautifiedFormat(it, item)
            tvChildReplyMsg.isVisible = true
        }
        tvChildReplyMsg1.isVisible = false
        subComments.getOrNull(1)?.let {
            tvChildReplyMsg1.setDefaultEmojiParser()
            tvChildReplyMsg1.text = getBeautifiedFormat(it, item)
            tvChildReplyMsg1.isVisible = true
        }
        // 如果是指示器，且该评论下盖楼的高度大于2则显示，否则隐藏
        tvChildReplyMsgAll.apply {
            text = "查看全部${buildHeight}条回复"
            isVisible = buildHeight > 2
        }
        itemView.setFixOnClickListener {
            mViewMoreClickListener.invoke(item, position)
        }
    }

    private fun getBeautifiedFormat(
        subComment: FishPondComment.FishPondCommentItem.SubComment,
        item: FishPondComment.FishPondCommentItem
    ): Spanned {
        // 谁回复的
        val whoReplied =
            subComment.getNickName() + if (subComment.getId() == item.getId()) "(作者)" else ""
        // 被回复的人
        val wasReplied = subComment.getTargetUserNickname()
        val content = whoReplied + "回复" + wasReplied + "：" + subComment.content
        val spannableString = SpannableString(content)
        val color = Color.parseColor("#045FB2")
        spannableString.setSpan(
            ForegroundColorSpan(color),
            content.indexOf(whoReplied),
            content.indexOf("回复"),
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        val startIndex = whoReplied.length + 2
        spannableString.setSpan(
            ForegroundColorSpan(color),
            startIndex,
            startIndex + wasReplied.length,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        return spannableString
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FishDetailCommendListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FishPondDetailCommendListBinding.inflate(inflater, parent, false)
        return FishDetailCommendListViewHolder(binding)
    }
}

class FishDetailCommendListViewHolder(val binding: FishPondDetailCommendListBinding) :
    RecyclerView.ViewHolder(binding.root)