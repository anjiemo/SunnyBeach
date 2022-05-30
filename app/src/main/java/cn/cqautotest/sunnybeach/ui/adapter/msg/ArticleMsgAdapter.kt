package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.ArticleMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.msg.ArticleMsg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils

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

        fun onBinding(item: ArticleMsg.Content?, position: Int) {
            item ?: return
            with(binding) {
                ivAvatar.loadAvatar(false, item.avatar)
                cbNickName.text = item.nickname
                val sdf = TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm")
                tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, sdf)
                tvReplyMsg.text = HtmlCompat.fromHtml(item.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
                tvChildReplyMsg.text = item.title
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