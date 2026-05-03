package cn.cqautotest.sunnybeach.ui.adapter

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.ImageView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
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
            photoIv.transitionName = item.id
            val imageWidth = photoIv.width
            val imageHeight = photoIv.height

            // 缩略图请求：通常已经在列表页缓存，可以快速加载并用于触发动画
            val thumbnailRequest = Glide.with(itemView)
                .load(item.thumb)
                .centerCrop()
                .dontAnimate() // 缩略图本身不需要动画，避免干扰共享元素
                .override(imageWidth, imageHeight)
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
                        // 缩略图就绪，通知界面启动动画
                        mOnImageLoadListener?.invoke(holder.bindingAdapterPosition)
                        return false
                    }
                })

            // 高清图请求
            Glide.with(itemView)
                .load(item.preview)
                .thumbnail(thumbnailRequest)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(300)) // 明确指定淡入淡出时长
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