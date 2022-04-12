package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.ShareListItemBinding
import cn.cqautotest.sunnybeach.model.UserShare
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/04/12
 * desc   : 用户分享列表的适配器
 */
class ShareListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserShare.Content, ShareListAdapter.ShareListViewHolder>(ShareDiffCallback()) {

    class ShareDiffCallback : DiffUtil.ItemCallback<UserShare.Content>() {
        override fun areItemsTheSame(
            oldItem: UserShare.Content,
            newItem: UserShare.Content
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserShare.Content,
            newItem: UserShare.Content
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class ShareListViewHolder(val binding: ShareListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: ShareListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShareListAdapter.ShareListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val tvShareTitle = binding.tvShareTitle
        val tvDesc = binding.tvDesc
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        tvShareTitle.text = item.title
        tvDesc.text = DateHelper.getFriendlyTimeSpanByNow("${item.createTime}:00")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShareListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ShareListItemBinding.inflate(inflater, parent, false)
        return ShareListViewHolder(binding)
    }
}