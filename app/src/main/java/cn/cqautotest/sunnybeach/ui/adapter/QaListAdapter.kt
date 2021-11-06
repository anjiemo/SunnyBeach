package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.QaListItemBinding
import cn.cqautotest.sunnybeach.model.UserQa
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener


/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户回答列表的适配器
 */
class QaListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserQa.Content, QaListAdapter.QaListViewHolder>(QaDiffCallback()) {

    class QaDiffCallback : DiffUtil.ItemCallback<UserQa.Content>() {
        override fun areItemsTheSame(
            oldItem: UserQa.Content,
            newItem: UserQa.Content
        ): Boolean {
            return oldItem.wendaComment.id == newItem.wendaComment.id
        }

        override fun areContentsTheSame(
            oldItem: UserQa.Content,
            newItem: UserQa.Content
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var mItemClickListener: (item: UserQa.Content, position: Int) -> Unit = { _, _ -> }

    fun setOnItemClickListener(block: (item: UserQa.Content, position: Int) -> Unit) {
        mItemClickListener = block
    }

    inner class QaListViewHolder(val binding: QaListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: QaListAdapter.QaListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val tvQaTitle = binding.tvQaTitle
        val tvDesc = binding.tvDesc
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
        tvQaTitle.text = item.wendaTitle
        tvDesc.text =
            DateHelper.getFriendlyTimeSpanByNow("${item.wendaComment.publishTime}:00")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QaListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = QaListItemBinding.inflate(inflater, parent, false)
        return QaListViewHolder(binding)
    }
}