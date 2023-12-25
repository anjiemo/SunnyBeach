package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spanned
import android.text.TextUtils
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.FishPondDetailCommendListBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 摸鱼话题评论列表适配器
 */
class FishPondDetailCommentListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<FishPondComment.FishPondCommentItem, FishDetailCommendListViewHolder>(diffCallback) {

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    private var mViewMoreClickListener: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit =
        { _, _ -> }
    private var mCommentClickListener: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit =
        { _, _ -> }

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
        ivAvatar.setFixOnClickListener {
            if (TextUtils.isEmpty(userId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(context, userId)
        }
        ivAvatar.loadAvatar(item.vip, item.avatar)
        ivPondComment.setFixOnClickListener {
            mCommentClickListener.invoke(item, position)
        }
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvNickName.text = item.getNickName()
        val job = item.position.ifNullOrEmpty { "滩友" }
        // 摸鱼详情列表的时间没有精确到秒
        tvDesc.text = "$job · " + TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
        tvReply.setDefaultEmojiParser()
        tvReply.text = item.content
        val subComments = item.subComments
        val buildHeight = subComments.size
        tvBuildReplyMsgContainer.isVisible = subComments.isNotEmpty()
        tvChildReplyMsg.isVisible = false
        tvChildReplyMsg.setDefaultEmojiParser()
        subComments.getOrNull(0)?.let {
            tvChildReplyMsg.text = getBeautifiedFormat(it, item)
            tvChildReplyMsg.isVisible = true
        }
        tvChildReplyMsg1.isVisible = false
        tvChildReplyMsg1.setDefaultEmojiParser()
        subComments.getOrNull(1)?.let {
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
        val whoReplied = subComment.getNickName() + if (subComment.getId() == item.getId()) "(作者)" else ""
        // 被回复的人
        val wasReplied = subComment.getTargetUserNickname()
        val color = Color.parseColor("#045FB2")
        return buildSpannedString {
            color(color) { append(whoReplied) }
            append(" 回复 ")
            color(color) { append(wasReplied) }
            append(" ：${subComment.content}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishDetailCommendListViewHolder =
        FishDetailCommendListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<FishPondComment.FishPondCommentItem>({ oldItem, newItem -> oldItem.getId() == newItem.getId() }) { oldItem, newItem -> oldItem == newItem }
    }
}

class FishDetailCommendListViewHolder(val binding: FishPondDetailCommendListBinding) : RecyclerView.ViewHolder(binding.root) {
    constructor(parent: ViewGroup) : this(parent.asViewBinding<FishPondDetailCommendListBinding>())
}