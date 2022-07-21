package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.MomentMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.msg.MomentMsg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import com.google.android.material.badge.BadgeUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 摸鱼评论列表消息适配器
 */
class MomentMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<MomentMsg.Content, MomentMsgAdapter.MomentMsgViewHolder>(diffCallback) {

    inner class MomentMsgViewHolder(val binding: MomentMsgListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<MomentMsgListItemBinding>())

        @SuppressLint("UnsafeOptInUsageError")
        fun onBinding(item: MomentMsg.Content?, position: Int) {
            item ?: return
            with(binding) {
                ivAvatar.loadAvatar(false, item.avatar)
                ivAvatar.post {
                    createDefaultStyleBadge(context, 0).apply {
                        BadgeUtils.attachBadgeDrawable(this, ivAvatar)
                        horizontalOffset = 12
                        verticalOffset = 12
                        isVisible = item.isRead.not()
                    }
                }
                cbNickName.text = item.nickname
                tvDesc.text = item.content
                val sdf = TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm")
                tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, sdf)
                tvReplyMsg.setDefaultEmojiParser()
                tvReplyMsg.text = item.content.parseAsHtml()
                tvChildReplyMsg.setDefaultEmojiParser()
                tvChildReplyMsg.text = item.title.parseAsHtml()
            }
        }
    }

    override fun onViewAttachedToWindow(holder: MomentMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: MomentMsgViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentMsgViewHolder = MomentMsgViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<MomentMsg.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}