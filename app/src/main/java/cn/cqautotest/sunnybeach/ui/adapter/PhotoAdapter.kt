package cn.cqautotest.sunnybeach.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.widget.RoundRelativeLayout
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hjq.widget.layout.RatioFrameLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/07
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
            roundLayout.setRadius((if (fillBox) 0 else 8).dp.toFloat())
            // 设置比例布局全屏
            if (fillBox) {
                // 设置比例布局全屏
                val ratioFrameLayout = getView<RatioFrameLayout>(R.id.ratio_frame_layout)
                ratioFrameLayout.setSizeRatio(
                    ScreenUtils.getScreenWidth().toFloat(),
                    ScreenUtils.getScreenHeight().toFloat()
                )
            } else {
                itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            // 加载全屏的图片
            Glide.with(itemView)
                .load(item.preview)
                .placeholder(R.mipmap.ic_bg).run {
                    if (fillBox) this else override(photoIv.width, photoIv.height)
                }
                .into(photoIv)
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
        loadingView.isVisible = show
    }
}