package cn.cqautotest.sunnybeach.ui.adapter.msg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.QaMsgListItemBinding
import cn.cqautotest.sunnybeach.model.msg.QAMsg
import cn.cqautotest.sunnybeach.ui.adapter.AdapterDelegate

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/25
 * desc   : 问答评论列表消息适配器
 */
class QAMsgAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<QAMsg.Content, QAMsgAdapter.QAMsgViewHolder>(object :
        DiffUtil.ItemCallback<QAMsg.Content>() {
        override fun areItemsTheSame(
            oldItem: QAMsg.Content,
            newItem: QAMsg.Content
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: QAMsg.Content,
            newItem: QAMsg.Content
        ): Boolean {
            return oldItem == newItem
        }
    }) {

    inner class QAMsgViewHolder(val binding: QaMsgListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: QAMsgViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: QAMsgViewHolder, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QAMsgViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QaMsgListItemBinding.inflate(inflater, parent, false)
        return QAMsgViewHolder(binding)
    }
}