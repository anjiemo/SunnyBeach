package cn.cqautotest.sunnybeach.ui.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.logByDebug
import cn.cqautotest.sunnybeach.widget.RoundRelativeLayout
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hjq.widget.layout.RatioFrameLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/9/7
 * desc   : 图片浏览适配器
 */
class PhotoAdapter(val fillBox: Boolean = false) :
    BaseQuickAdapter<WallpaperBean.Res.Vertical, BaseViewHolder>(R.layout.photo_list_item),
    LoadMoreModule {

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
            if (fillBox) {
                itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            val photoIv = getView<ImageView>(R.id.photoIv)
            val roundLayout = getView<RoundRelativeLayout>(R.id.round_layout)
            val loadingView = getView<View>(R.id.loading_lav)
            showLoading(loadingView, fillBox)
            if (fillBox.not()) {
                roundLayout.setRadius(8.dp.toFloat())
                // 加载非全屏的图片列表
                logByDebug(msg = "convert：==> width is ${photoIv.width}")
                Glide.with(itemView)
                    .load(item.thumb)
                    .placeholder(R.mipmap.ic_bg)
                    .override(photoIv.width, photoIv.height)
                    .into(photoIv)
            } else {
                roundLayout.setRadius(0f)
                // 设置比例布局全屏
                val ratioFrameLayout = getView<RatioFrameLayout>(R.id.ratio_frame_layout)
                ratioFrameLayout.setSizeRatio(
                    ScreenUtils.getScreenWidth().toFloat(),
                    ScreenUtils.getScreenHeight().toFloat()
                )
                // 加载全屏的图片
                Glide.with(itemView)
                    .load(item.preview)
                    .placeholder(R.mipmap.ic_bg)
                    .into(object : ImageViewTarget<Drawable>(photoIv) {

                        override fun onLoadStarted(placeholder: Drawable?) {
                            super.onLoadStarted(placeholder)
                            showLoading(loadingView, true)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            showLoading(loadingView, false)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            super.onLoadCleared(placeholder)
                            showLoading(loadingView, false)
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            super.onResourceReady(resource, transition)
                            showLoading(loadingView, false)
                        }

                        override fun setResource(resource: Drawable?) {
                            getView().setImageDrawable(resource)
                        }
                    })
            }
            itemView.setOnClickListener {
                mItemClickListener(item, bindingAdapterPosition)
            }
            itemView.setOnLongClickListener {
                mItemLongClickListener(item, bindingAdapterPosition)
                true
            }
        }
    }

    private fun showLoading(loadingView: View, show: Boolean) {
        loadingView.visibility = if (show) View.VISIBLE else View.GONE
    }
}