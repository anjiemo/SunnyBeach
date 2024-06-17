package cn.cqautotest.sunnybeach.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.UserQaListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.SearchResult
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索结果列表的适配器
 */
class SearchResultListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<SearchResult.SearchResultItem, SearchResultListAdapter.SearchResultListViewHolder>(diffCallback) {

    inner class SearchResultListViewHolder(val binding: UserQaListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<UserQaListItemBinding>())

        // Replace image with empty drawable.
        private val emptyDrawable = ColorDrawable()

        fun onBind(item: SearchResult.SearchResultItem?, position: Int) {
            item ?: return
            with(binding) {
                tvQaTitle.text = item.title.replace("\n", "<br>", true).parseAsHtml()
                // We do not need to display image information in the search results,
                // so as to avoid typographical confusion caused by images.
                tvDesc.text = item.content.replace("\n", "<br>", true).parseAsHtml(imageGetter = { emptyDrawable })
            }
        }
    }

    override fun onViewAttachedToWindow(holder: SearchResultListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: SearchResultListAdapter.SearchResultListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBind(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultListViewHolder = SearchResultListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<SearchResult.SearchResultItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}