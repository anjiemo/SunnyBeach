package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.AtMeMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.msg.AtMeMsg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : @我 列表消息适配器
 */
class AtMeMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<AtMeMsg.Content, AtMeMsgAdapter.AtMeMsgViewHolder>(diffCallback) {

    inner class AtMeMsgViewHolder(val binding: AtMeMsgListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<AtMeMsgListItemBinding>())

        fun onBinding(item: AtMeMsg.Content?, position: Int) {
            item ?: return
            with(binding) {
                ivAvatar.loadAvatar(false, item.avatar)
                cbNickName.text = item.nickname
                val sdf = TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm")
                tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.publishTime, sdf)
                tvReplyMsg.height = 0
                tvChildReplyMsg.text = HtmlCompat.fromHtml(item.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: AtMeMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: AtMeMsgViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtMeMsgViewHolder = AtMeMsgViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<AtMeMsg.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}