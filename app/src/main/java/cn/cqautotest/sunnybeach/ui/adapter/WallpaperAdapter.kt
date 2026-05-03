package cn.cqautotest.sunnybeach.ui.adapter

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
    private var mOnImageLoadListener: ((position: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit) {
        mItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit) {
        mItemLongClickListener = listener
    }

    fun setOnImageLoadListener(listener: (position: Int) -> Unit) {
        mOnImageLoadListener = listener
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: WallpaperBean.Res.Vertical?) {
        item ?: return
        holder.run {
            val photoIv = getView<ImageView>(R.id.photoIv)
            // 初始设置为 CENTER_CROP，保证转场瞬间与列表页 100% 一致，避免比例计算冲突
            photoIv.scaleType = ImageView.ScaleType.CENTER_CROP
            photoIv.transitionName = item.id

            // 缩略图请求
            val thumbnailRequest = Glide.with(itemView)
                .load(item.thumb)
                .centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        mOnImageLoadListener?.invoke(holder.bindingAdapterPosition)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        mOnImageLoadListener?.invoke(holder.bindingAdapterPosition)
                        return false
                    }
                })

            // 高清图请求
            Glide.with(itemView)
                .load(item.preview)
                .thumbnail(thumbnailRequest)
                .format(DecodeFormat.PREFER_RGB_565)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                        // 加载失败也切换到 FIT_CENTER 确保至少能看到缩略图的全貌
                        photoIv.scaleType = ImageView.ScaleType.FIT_CENTER
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        // 高清图就绪后，切换为 FIT_CENTER 以完整展示，此时转场动画已基本结束，不会产生冲突
                        photoIv.scaleType = ImageView.ScaleType.FIT_CENTER
                        return false
                    }
                })
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