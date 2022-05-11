package cn.cqautotest.sunnybeach.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.UserQaListItemBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.SearchResult

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索结果列表的适配器
 */
class SearchResultListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<SearchResult.SearchResultItem, SearchResultListAdapter.SearchResultListViewHolder>(SearchResultDiffCallback()) {

    class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResult.SearchResultItem>() {
        override fun areItemsTheSame(
            oldItem: SearchResult.SearchResultItem,
            newItem: SearchResult.SearchResultItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: SearchResult.SearchResultItem,
            newItem: SearchResult.SearchResultItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class SearchResultListViewHolder(val binding: UserQaListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: SearchResult.SearchResultItem, position: Int) {
            binding.tvQaTitle.text = item.title.parseAsHtml()
            binding.tvDesc.text = item.content.parseAsHtml()
        }
    }

    override fun onViewAttachedToWindow(holder: SearchResultListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: SearchResultListAdapter.SearchResultListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        holder.onBind(item, position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserQaListItemBinding.inflate(inflater, parent, false)
        return SearchResultListViewHolder(binding)
    }
}