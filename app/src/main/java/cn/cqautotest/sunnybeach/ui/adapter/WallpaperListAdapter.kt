package cn.cqautotest.sunnybeach.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.PhotoListItemBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
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

    inner class PhotoListViewHolder(private val binding: PhotoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun binding(position: Int) {
            val photoIv = binding.photoIv
            val ratioFrameLayout = binding.ratioFrameLayout
            val item = getItem(position) ?: return
            // 设置比例布局全屏
            if (fillBox) {
                ratioFrameLayout.setSizeRatio(
                    ScreenUtils.getScreenWidth().toFloat(),
                    ScreenUtils.getScreenHeight().toFloat()
                )
            } else {
                itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            val imageUrl = if (fillBox) item.preview else item.thumb
            // 加载全屏的图片
            Glide.with(itemView)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_bg)
                .into(photoIv)
            itemView.setFixOnClickListener {
                photoIv.transitionName = item.id
                mItemClickListener.invoke(photoIv, item, position)
            }
            itemView.setOnLongClickListener {
                mItemLongClickListener.invoke(item, position)
                true
            }
        }
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
        holder.binding(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhotoListItemBinding.inflate(inflater, parent, false)
        return PhotoListViewHolder(binding)
    }
}