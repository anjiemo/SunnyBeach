package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/07
 * desc   : 图片浏览适配器
 */
class WallpaperAdapter : BaseQuickAdapter<WallpaperBean.Res.Vertical, QuickViewHolder>(itemDiffCallback({ old, new -> old.id == new.id }, { old, new -> old == new })) {

    private var mItemClickListener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit =
        { _, _ -> }
    private var mItemLongClickListener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit =
        { _, _ -> }

    fun setOnItemClickListener(listener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit) {
        mItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit) {
        mItemLongClickListener = listener
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: WallpaperBean.Res.Vertical?) {
        item ?: return
        holder.run {
            val photoIv = getView<ImageView>(R.id.photoIv)
            val imageWidth = photoIv.width
            val imageHeight = photoIv.height
            val thumbnailRequest = Glide.with(itemView)
                .load(item.thumb)
                .centerCrop()
                .override(imageWidth, imageHeight)
            Glide.with(itemView)
                .load(item.preview)
                .thumbnail(thumbnailRequest)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(imageWidth, imageHeight)
                .into(photoIv)
            itemView.setOnClickListener {
                mItemClickListener(item, holder.bindingAdapterPosition)
            }
            itemView.setOnLongClickListener {
                mItemLongClickListener(item, holder.bindingAdapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(context: android.content.Context, parent: ViewGroup, viewType: Int): QuickViewHolder {
        return QuickViewHolder(R.layout.wallpaper_list_item, parent)
    }
}