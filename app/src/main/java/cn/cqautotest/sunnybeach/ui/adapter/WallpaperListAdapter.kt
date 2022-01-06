package cn.cqautotest.sunnybeach.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.PhotoListItemBinding
import cn.cqautotest.sunnybeach.http.response.model.WallpaperBean
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 图片列表的适配器
 */
class WallpaperListAdapter(
    private val adapterDelegate: AdapterDelegate,
    private val fillBox: Boolean = false
) :
    PagingDataAdapter<WallpaperBean.Res.Vertical, WallpaperListAdapter.PhotoListViewHolder>(
        WallDiffCallback()
    ) {

    class WallDiffCallback : DiffUtil.ItemCallback<WallpaperBean.Res.Vertical>() {
        override fun areItemsTheSame(
            oldItem: WallpaperBean.Res.Vertical,
            newItem: WallpaperBean.Res.Vertical
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: WallpaperBean.Res.Vertical,
            newItem: WallpaperBean.Res.Vertical
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class PhotoListViewHolder(binding: PhotoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photoIv = binding.photoIv
        val ratioFrameLayout = binding.ratioFrameLayout
    }

    override fun onViewAttachedToWindow(holder: PhotoListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    private var mItemClickListener: (view: View, verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit =
        { _, _, _ -> }


    private var mItemLongClickListener: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit =
        { _, _ -> }

    fun setOnItemClickListener(block: (view: View, verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit) {
        mItemClickListener = block
    }

    fun setOnItemLongClickListener(block: (verticalPhoto: WallpaperBean.Res.Vertical, position: Int) -> Unit) {
        mItemLongClickListener = block
    }

    override fun onBindViewHolder(holder: PhotoListViewHolder, position: Int) {
        val itemView = holder.itemView
        // 设置比例布局全屏
        val ratioFrameLayout = holder.ratioFrameLayout
        val photoIv = holder.photoIv
        val item = getItem(position) ?: return
        if (fillBox) {
            itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            itemView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        if (fillBox.not()) {
            // 加载非全屏的图片列表
            Glide.with(itemView)
                .load(item.thumb)
                .placeholder(R.mipmap.ic_bg)
                .into(photoIv)
        } else {
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
        photoIv.transitionName = item.id
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(photoIv, item, position)
        }
        itemView.setOnLongClickListener {
            mItemLongClickListener.invoke(item, position)
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhotoListItemBinding.inflate(inflater, parent, false)
        return PhotoListViewHolder(binding)
    }
}