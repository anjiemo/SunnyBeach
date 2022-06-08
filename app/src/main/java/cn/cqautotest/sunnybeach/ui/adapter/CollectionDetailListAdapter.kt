package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.CollectionDetailListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.BookmarkDetail
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹详情列表的适配器
 */
class CollectionDetailListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<BookmarkDetail.Content, CollectionDetailListAdapter.CollectionDetailListViewHolder>(diffCallback) {

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    inner class CollectionDetailListViewHolder(val binding: CollectionDetailListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<CollectionDetailListItemBinding>())

        fun onBinding(item: BookmarkDetail.Content?, position: Int) {
            item ?: return
            with(binding) {
                tvTitle.text = item.title
                tvLabel.text = ""
                tvDesc.text = TimeUtils.getFriendlyTimeSpanByNow(item.addTime, mSdf)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: CollectionDetailListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: CollectionDetailListAdapter.CollectionDetailListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionDetailListViewHolder =
        CollectionDetailListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<BookmarkDetail.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}