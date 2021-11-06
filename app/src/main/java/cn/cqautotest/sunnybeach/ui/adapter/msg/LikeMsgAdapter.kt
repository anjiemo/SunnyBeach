package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.LikeMsgListItemBinding
import cn.cqautotest.sunnybeach.model.msg.LikeMsg
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 点赞列表消息适配器
 */
class LikeMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<LikeMsg.Content, LikeMsgAdapter.LikeMsgViewHolder>(object :
        DiffUtil.ItemCallback<LikeMsg.Content>() {
        override fun areItemsTheSame(
            oldItem: LikeMsg.Content,
            newItem: LikeMsg.Content
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LikeMsg.Content,
            newItem: LikeMsg.Content
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class LikeMsgViewHolder(val binding: LikeMsgListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: LikeMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: LikeMsgViewHolder, position: Int) {
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
        // flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        cbNickName.text = item.nickname
        tvDesc.text = item.timeText
        tvReplyMsg.height = 0
        tvChildReplyMsg.text = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeMsgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LikeMsgListItemBinding.inflate(inflater, parent, false)
        return LikeMsgViewHolder(binding)
    }
}