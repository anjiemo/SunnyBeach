package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.ArticleMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.msg.ArticleMsg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import com.google.android.material.badge.BadgeUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 文章评论列表消息适配器
 */
class ArticleMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<ArticleMsg.Content, ArticleMsgAdapter.ArticleMsgViewHolder>(diffCallback) {

    inner class ArticleMsgViewHolder(val binding: ArticleMsgListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<ArticleMsgListItemBinding>())

        @SuppressLint("UnsafeOptInUsageError")
        fun onBinding(item: ArticleMsg.Content?, position: Int) {
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
                val sdf = TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm")
                tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, sdf)
                tvReplyMsg.setDefaultEmojiParser()
                tvReplyMsg.text = item.content.replace("\n", "<br>", true).parseAsHtml()
                tvChildReplyMsg.setDefaultEmojiParser()
                tvChildReplyMsg.text = item.title.parseAsHtml()
            }
        }
    }

    override fun onViewAttachedToWindow(holder: ArticleMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: ArticleMsgViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleMsgViewHolder = ArticleMsgViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<ArticleMsg.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}