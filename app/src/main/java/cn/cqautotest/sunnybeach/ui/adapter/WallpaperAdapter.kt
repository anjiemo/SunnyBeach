package cn.cqautotest.sunnybeach.ui.adapter

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/07
 * desc   : 图片浏览适配器
 */
class WallpaperAdapter : BaseQuickAdapter<WallpaperBean.Res.Vertical, BaseViewHolder>(R.layout.wallpaper_list_item), LoadMoreModule {

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

    override fun convert(holder: BaseViewHolder, item: WallpaperBean.Res.Vertical) {
        holder.run {
            val photoIv = getView<ImageView>(R.id.photoIv)
            Glide.with(itemView)
                .load(item.preview)
                .centerCrop()
                .into(photoIv)
            with(holder as RecyclerView.ViewHolder) {
                itemView.setOnClickListener {
                    mItemClickListener(item, bindingAdapterPosition)
                }
                itemView.setOnLongClickListener {
                    mItemLongClickListener(item, bindingAdapterPosition)
                    true
                }
            }
        }
    }
}