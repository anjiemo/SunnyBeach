package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.MomentMsgListItemBinding
import cn.cqautotest.sunnybeach.model.msg.MomentMsg
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 摸鱼评论列表消息适配器
 */
class MomentMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<MomentMsg.Content, MomentMsgAdapter.MomentMsgViewHolder>(object :
        DiffUtil.ItemCallback<MomentMsg.Content>() {
        override fun areItemsTheSame(
            oldItem: MomentMsg.Content,
            newItem: MomentMsg.Content
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MomentMsg.Content,
            newItem: MomentMsg.Content
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class MomentMsgViewHolder(val binding: MomentMsgListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: MomentMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: MomentMsgViewHolder, position: Int) {
        val itemView = holder.itemView
        val binding = holder.binding
        val flAvatarContainer = binding.flAvatarContainer
        val ivAvatar = binding.ivAvatar
        val cbNickName = binding.cbNickName
        val tvDesc = binding.tvDesc
        val tvReplyMsg = binding.tvReplyMsg
        val tvChildReplyMsg = binding.tvChildReplyMsg
        val context = itemView.context
        val item = getItem(position) ?: return
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        // flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        cbNickName.text = item.nickname
        tvDesc.text = item.content
        val sdf = TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm")
        tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, sdf)
        tvReplyMsg.text = item.content
        tvChildReplyMsg.text = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentMsgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MomentMsgListItemBinding.inflate(inflater, parent, false)
        return MomentMsgViewHolder(binding)
    }
}