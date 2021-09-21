package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FishPondDetailCommendListBinding
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/07/11
 * desc   : 摸鱼话题评论列表适配器
 */
class FishPondDetailCommendListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<FishPondComment.FishPondCommentItem, FishDetailCommendListViewHolder>(
        object :
            DiffUtil.ItemCallback<FishPondComment.FishPondCommentItem>() {
            override fun areItemsTheSame(
                oldItem: FishPondComment.FishPondCommentItem,
                newItem: FishPondComment.FishPondCommentItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FishPondComment.FishPondCommentItem,
                newItem: FishPondComment.FishPondCommentItem
            ): Boolean {
                return oldItem == newItem
            }
        }) {

    private var mItemClickListener: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit =
        { _, _ -> }
    private var mViewMoreClickListener: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit =
        { _, _ -> }

    fun setOnItemClickListener(block: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit) {
        mItemClickListener = block
    }

    fun setOnVewMoreClickListener(block: (item: FishPondComment.FishPondCommentItem, position: Int) -> Unit) {
        mViewMoreClickListener = block
    }

    override fun onViewAttachedToWindow(holder: FishDetailCommendListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FishDetailCommendListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val flAvatarContainer = binding.flAvatarContainer
        val ivAvatar = binding.ivFishPondAvatar
        val tvNickname = binding.cbFishPondNickName
        val tvDesc = binding.tvFishPondDesc
        val tvReply = binding.tvReplyMsg
        val tvBuildReplyMsgContainer = binding.tvBuildReplyMsgContainer
        val tvChildReplyMsg = binding.tvChildReplyMsg
        val tvChildReplyMsg1 = binding.tvChildReplyMsg1
        val tvChildReplyMsgAll = binding.tvChildReplyMsgAll
        val context = itemView.context
        flAvatarContainer.background = if (item.vip) ContextCompat.getDrawable(
            context,
            R.drawable.avatar_circle_vip_ic
        ) else null
        Glide.with(holder.itemView)
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
        tvNickname.text = item.nickname
        // 摸鱼详情列表的时间没有精确到秒
        tvDesc.text = "${item.position} · " +
                DateHelper.transform2FriendlyTimeSpanByNow("${item.createTime}:00")
        tvReply.text = item.content
        val subComments = item.subComments
        val buildHeight = subComments.size
        tvBuildReplyMsgContainer.visibility =
            if (subComments.isNotEmpty()) View.VISIBLE else View.GONE
        tvChildReplyMsg.visibility = View.GONE
        subComments.getOrNull(0)?.let {
            tvChildReplyMsg.text = getBeautifiedFormat(it, item)
            tvChildReplyMsg.visibility = View.VISIBLE
        }
        tvChildReplyMsg1.visibility = View.GONE
        subComments.getOrNull(1)?.let {
            tvChildReplyMsg1.text = getBeautifiedFormat(it, item)
            tvChildReplyMsg1.visibility = View.VISIBLE
        }
        // 如果是指示器，且该评论下盖楼的高度大于2则显示，否则隐藏
        tvChildReplyMsgAll.apply {
            text = "查看全部${buildHeight}条回复"
            visibility = if (buildHeight > 2) View.VISIBLE else View.GONE
        }
        tvChildReplyMsgAll.setFixOnClickListener {
            mViewMoreClickListener.invoke(item, position)
        }
    }

    private fun getBeautifiedFormat(
        subComment: FishPondComment.FishPondCommentItem.SubComment,
        item: FishPondComment.FishPondCommentItem
    ): Spanned {
        val whoReplied = subComment.nickname + if (subComment.id == item.id) "(作者)" else ""
        val wasReplied = subComment.targetUserNickname
        val content = whoReplied + "回复" + wasReplied + "：" + subComment.content
        val spannableString = SpannableString(content)
        val color = Color.parseColor("#045FB2")
        spannableString.setSpan(
            ForegroundColorSpan(color),
            content.indexOf(whoReplied),
            content.indexOf("回复"),
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color),
            content.indexOf(wasReplied),
            content.indexOf(wasReplied) + wasReplied.length,
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