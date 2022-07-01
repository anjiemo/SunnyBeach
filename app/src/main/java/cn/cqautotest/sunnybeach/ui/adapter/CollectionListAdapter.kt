package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.CollectionListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.Bookmark
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏夹列表的适配器
 */
class CollectionListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<Bookmark.Content, CollectionListAdapter.CollectionListViewHolder>(diffCallback) {

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    inner class CollectionListViewHolder(val binding: CollectionListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<CollectionListItemBinding>())

        fun onBinding(item: Bookmark.Content?, position: Int) {
            item ?: return
            with(binding) {
                Glide.with(itemView)
                    .load(item.cover)
                    .into(ivCover)
                tvName.text = item.name
                tvDesc.text = item.description
                tvCreateTime.text = TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: CollectionListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: CollectionListAdapter.CollectionListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionListViewHolder = CollectionListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<Bookmark.Content>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}