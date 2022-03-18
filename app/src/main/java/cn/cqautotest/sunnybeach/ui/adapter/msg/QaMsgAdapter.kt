package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.QaMsgListItemBinding
import cn.cqautotest.sunnybeach.model.msg.QaMsg
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 问答评论列表消息适配器
 */
class QaMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<QaMsg.Content, QaMsgAdapter.QAMsgViewHolder>(QaMsgDiffCallback()) {

    class QaMsgDiffCallback : DiffUtil.ItemCallback<QaMsg.Content>() {
        override fun areItemsTheSame(oldItem: QaMsg.Content, newItem: QaMsg.Content): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QaMsg.Content, newItem: QaMsg.Content): Boolean {
            return oldItem == newItem
        }
    }

    inner class QAMsgViewHolder(val binding: QaMsgListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: QAMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: QAMsgViewHolder, position: Int) {
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
        tvDesc.text = item.timeText
        tvReplyMsg.height = 0
        val preText = "回答了朕的提问：「"
        val qaTitle = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val suffixText = "」去看看问题解决了吗？"
        val ss = SpannableString(preText + qaTitle + suffixText)
        if (TextUtils.isEmpty(qaTitle).not()) {
            val highlightColor = Color.parseColor("#1D7DFA")
            val fcs = ForegroundColorSpan(highlightColor)
            val startIndex = preText.length
            val endIndex = preText.length + qaTitle.length
            ss.setSpan(fcs, startIndex, endIndex, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
        }
        tvChildReplyMsg.text = ss
        tvChildReplyMsg.ellipsize = TextUtils.TruncateAt.MIDDLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QAMsgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QaMsgListItemBinding.inflate(inflater, parent, false)
        return QAMsgViewHolder(binding)
    }
}