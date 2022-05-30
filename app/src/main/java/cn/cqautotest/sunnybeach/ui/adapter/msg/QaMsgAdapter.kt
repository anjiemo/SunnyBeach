package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.graphics.Color
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.text.buildSpannedString
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.QaMsgListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.msg.QaMsg
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate

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

        fun onBinding(item: QaMsg.Content?, position: Int) {
            item ?: return
            with(binding) {
                cbNickName.text = item.nickname
                ivAvatar.loadAvatar(false, item.avatar)
                tvDesc.text = item.timeText
                tvReplyMsg.height = 0
                val preText = "回答了朕的提问：「"
                val qaTitle = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
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