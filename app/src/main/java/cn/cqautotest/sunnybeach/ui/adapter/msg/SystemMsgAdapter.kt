package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.SystemMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.msg.SystemMsg
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 系统列表消息适配器
 */
class SystemMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<SystemMsg.Content, SystemMsgAdapter.SystemMsgViewHolder>(SystemMsgDiffCallback()) {

    class SystemMsgDiffCallback : DiffUtil.ItemCallback<SystemMsg.Content>() {
        override fun areItemsTheSame(
            oldItem: SystemMsg.Content,
            newItem: SystemMsg.Content
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SystemMsg.Content,
            newItem: SystemMsg.Content
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class SystemMsgViewHolder(val binding: SystemMsgListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: SystemMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: SystemMsgViewHolder, position: Int) {
        val itemView = holder.itemView
        val binding = holder.binding
        val ivAvatar = binding.ivAvatar
        val cbNickName = binding.cbNickName
        val tvDesc = binding.tvDesc
        val tvReplyMsg = binding.tvReplyMsg
        val tvChildReplyMsg = binding.tvChildReplyMsg
        val item = getItem(position) ?: return
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        Glide.with(itemView)
            .load(R.mipmap.ic_gold)
            .placeholder(R.mipmap.ic_gold)
            .error(R.mipmap.ic_gold)
            .circleCrop()
            .into(ivAvatar)
        cbNickName.text = item.title
        val sdf = TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm")
        tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.publishTime, sdf)
        tvReplyMsg.height = 0
        tvChildReplyMsg.text = HtmlCompat.fromHtml(item.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SystemMsgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SystemMsgListItemBinding.inflate(inflater, parent, false)
        return SystemMsgViewHolder(binding)
    }
}