package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.AtMeMsgListItemBinding
import cn.cqautotest.sunnybeach.model.msg.ArticleMsg
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : @我 列表消息适配器
 */
class AtMeMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<ArticleMsg.Content, AtMeMsgAdapter.AtMeMsgViewHolder>(object :
        DiffUtil.ItemCallback<ArticleMsg.Content>() {
        override fun areItemsTheSame(
            oldItem: ArticleMsg.Content,
            newItem: ArticleMsg.Content
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ArticleMsg.Content,
            newItem: ArticleMsg.Content
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class AtMeMsgViewHolder(val binding: AtMeMsgListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: AtMeMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: AtMeMsgViewHolder, position: Int) {
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
        // flAvatarContainer.background = if (item.vip) ContextCompat.getDrawable(
        //     context,
        //     R.drawable.avatar_circle_vip_ic
        // ) else null
        Glide.with(itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        cbNickName.text = item.nickname
        val sdf = TimeUtils.getSafeDateFormat("yyyy-MM-dd HH:mm")
        tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, sdf)
        tvReplyMsg.height = 0
        tvChildReplyMsg.text = item.content
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtMeMsgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AtMeMsgListItemBinding.inflate(inflater, parent, false)
        return AtMeMsgViewHolder(binding)
    }
}