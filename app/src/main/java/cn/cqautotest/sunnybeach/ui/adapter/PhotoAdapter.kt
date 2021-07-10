package cn.cqautotest.sunnybeach.ui.adapter

import android.view.ViewGroup
import android.widget.ImageView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.http.response.model.HomePhotoBean
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hjq.widget.layout.RatioFrameLayout

class PhotoAdapter(val fillBox: Boolean = false) :
    BaseQuickAdapter<HomePhotoBean.Res.Vertical, BaseViewHolder>(R.layout.photo_item),
    LoadMoreModule {

    private lateinit var mItemClickListener: ((verticalPhoto: HomePhotoBean.Res.Vertical, position: Int) -> Unit)
    private lateinit var mItemLongClickListener: ((verticalPhoto: HomePhotoBean.Res.Vertical, position: Int) -> Unit)

    fun setOnItemClickListener(listener: (verticalPhoto: HomePhotoBean.Res.Vertical, position: Int) -> Unit) {
        mItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (verticalPhoto: HomePhotoBean.Res.Vertical, position: Int) -> Unit) {
        mItemLongClickListener = listener
    }

    override fun convert(holder: BaseViewHolder, item: HomePhotoBean.Res.Vertical) {
        holder.run {
            if (fillBox) {
                itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            val photoIv = getView<ImageView>(R.id.photoIv)
            if (fillBox.not()) {
                // 加载非全屏的图片列表
                Glide.with(itemView)
                    .load(item.thumb)
                    .placeholder(R.mipmap.ic_bg)
                    .into(photoIv)
            } else {
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
                    .into(photoIv)
            }
            itemView.setOnClickListener {
                if (::mItemClickListener.isInitialized) {
                    mItemClickListener(item, position)
                }
            }
            itemView.setOnLongClickListener {
                if (::mItemLongClickListener.isInitialized) {
                    mItemLongClickListener(item, position)
                }
                true
            }
        }
    }
}