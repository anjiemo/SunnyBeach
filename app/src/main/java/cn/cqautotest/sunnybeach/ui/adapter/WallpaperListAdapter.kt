package cn.cqautotest.sunnybeach.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.PhotoListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.model.wallpaper.WallpaperBean
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
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
) : PagingDataAdapter<WallpaperBean.Res.Vertical, WallpaperListAdapter.PhotoListViewHolder>(diffCallback) {

    private val screenWidth = ScreenUtils.getScreenWidth().toFloat()
    private val screenHeight = ScreenUtils.getScreenHeight().toFloat()

    inner class PhotoListViewHolder(private val binding: PhotoListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<PhotoListItemBinding>())

        fun onBinding(item: WallpaperBean.Res.Vertical?, position: Int) {
            item ?: return
            with(binding) {
                // 设置比例布局全屏
                if (fillBox) {
                    ratioFrameLayout.setSizeRatio(screenWidth, screenHeight)
                } else {
                    itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                    itemView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                // 加载全屏的图片
                Glide.with(itemView)
                    .load(item.thumb)
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
        holder.itemView.setOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListViewHolder = PhotoListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<WallpaperBean.Res.Vertical>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}