package cn.cqautotest.sunnybeach.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.UserQaListItemBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.SearchResult

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索结果列表的适配器
 */
class SearchResultListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<SearchResult.SearchResultItem, SearchResultListAdapter.SearchResultListViewHolder>(diffCallback) {

    inner class SearchResultListViewHolder(val binding: UserQaListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Replace image with empty drawable.
        private val emptyDrawable = ColorDrawable()

        fun onBind(item: SearchResult.SearchResultItem, position: Int) {
            binding.tvQaTitle.text = item.title.parseAsHtml()
            // We do not need to display image information in the search results,
            // so as to avoid typographical confusion caused by images.
            binding.tvDesc.text = item.content.parseAsHtml(imageGetter = { emptyDrawable })
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

    companion object {

        private val diffCallback =
            itemDiffCallback<SearchResult.SearchResultItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}