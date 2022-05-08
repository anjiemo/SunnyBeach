package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.UserQaListItemBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.UserQa
import cn.cqautotest.sunnybeach.util.DateHelper

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户回答列表的适配器
 */
class UserQaListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserQa.Content, UserQaListAdapter.QaListViewHolder>(UserQaDiffCallback()) {

    class UserQaDiffCallback : DiffUtil.ItemCallback<UserQa.Content>() {
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

    inner class QaListViewHolder(val binding: UserQaListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: QaListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserQaListAdapter.QaListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val tvQaTitle = binding.tvQaTitle
        val tvDesc = binding.tvDesc
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        tvQaTitle.text = item.wendaTitle
        tvDesc.text = DateHelper.getFriendlyTimeSpanByNow("${item.wendaComment.publishTime}:00")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QaListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserQaListItemBinding.inflate(inflater, parent, false)
        return QaListViewHolder(binding)
    }
}