package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.PhotoListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 图片列表的适配器
 */
class PhotoListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<WallpaperBean.Res.Vertical, PhotoListAdapter.PhotoListViewHolder>(diffCallback) {

    inner class PhotoListViewHolder(private val binding: PhotoListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<PhotoListItemBinding>())

        fun onBinding(item: WallpaperBean.Res.Vertical?, position: Int) {
            item ?: return
            with(binding) {
                Glide.with(itemView)
                    .load(item.thumb)
                    .centerCrop()
                    .into(photoIv)
                itemView.setFixOnClickListener {
                    photoIv.transitionName = item.id
                    adapterDelegate.onItemClick(it, bindingAdapterPosition)
                }
                itemView.setOnLongClickListener {
                    mItemLongClickListener.invoke(item, bindingAdapterPosition)
                    true
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: PhotoListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    private var mItemLongClickListener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit =
        { _, _ -> }

    fun setOnItemLongClickListener(block: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit) {
        mItemLongClickListener = block
    }

    override fun onBindViewHolder(holder: PhotoListViewHolder, position: Int) {
        holder.itemView.setOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListViewHolder = PhotoListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<WallpaperBean.Res.Vertical>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}