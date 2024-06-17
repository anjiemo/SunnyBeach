package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.QaMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.model.msg.QaMsg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.google.android.material.badge.BadgeUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 问答评论列表消息适配器
 */
class QaMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<QaMsg.Content, QaMsgAdapter.QAMsgViewHolder>(diffCallback) {

    inner class QAMsgViewHolder(val binding: QaMsgListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<QaMsgListItemBinding>())

        @SuppressLint("UnsafeOptInUsageError")
        fun onBinding(item: QaMsg.Content?, position: Int) {
            item ?: return
            with(binding) {
                cbNickName.text = item.nickname
                ivAvatar.loadAvatar(false, item.avatar)
                ivAvatar.post {
                    createDefaultStyleBadge(context, 0).apply {
                        BadgeUtils.attachBadgeDrawable(this, ivAvatar)
                        horizontalOffset = 12
                        verticalOffset = 12
                        isVisible = item.isRead.not()
                    }
                }
                tvDesc.text = item.timeText
                tvReplyMsg.height = 0
                val preText = "回答了朕的提问：「"
                val qaTitle = item.title.replace("\n", "<br>", true).parseAsHtml()
                val suffixText = "」去看看问题解决了吗？"
                tvChildReplyMsg.text = buildSpannedString {
                    append(preText + qaTitle + suffixText)
                    if (TextUtils.isEmpty(qaTitle).not()) {
                        val highlightColor = Color.parseColor("#1D7DFA")
                        val fcs = ForegroundColorSpan(highlightColor)
                        val startIndex = preText.length
                        val endIndex = preText.length + qaTitle.length
                        setSpan(fcs, startIndex, endIndex, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }
                tvChildReplyMsg.ellipsize = TextUtils.TruncateAt.MIDDLE
            }
        }
    }

    override fun onViewAttachedToWindow(holder: QAMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: QAMsgViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QAMsgViewHolder = QAMsgViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<QaMsg.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}