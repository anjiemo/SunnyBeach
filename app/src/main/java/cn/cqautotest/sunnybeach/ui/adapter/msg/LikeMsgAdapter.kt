package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.LikeMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.createDefaultStyleBadge
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.msg.LikeMsg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.google.android.material.badge.BadgeUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 点赞列表消息适配器
 */
class LikeMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<LikeMsg.Content, LikeMsgAdapter.LikeMsgViewHolder>(diffCallback) {

    inner class LikeMsgViewHolder(val binding: LikeMsgListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<LikeMsgListItemBinding>())

        @SuppressLint("UnsafeOptInUsageError")
        fun onBinding(item: LikeMsg.Content?, position: Int) {
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
                tvDesc.text = item.timeText
                tvReplyMsg.height = 0
                tvChildReplyMsg.setDefaultEmojiParser()
                tvChildReplyMsg.text = item.title.replace("\n", "<br>", true).parseAsHtml()
            }
        }
    }

    override fun onViewAttachedToWindow(holder: LikeMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: LikeMsgViewHolder, position: Int) {
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeMsgViewHolder = LikeMsgViewHolder(parent).also { holder ->
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        with(holder.binding) {
            ivAvatar.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
            llTopContainer.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
            tvDesc.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        }
    }

    companion object {

        private val diffCallback =
            itemDiffCallback<LikeMsg.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}