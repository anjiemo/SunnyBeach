package com.example.blogsystem.adapter

import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.blogsystem.R
import com.example.blogsystem.network.model.HomePhotoBean

class PhotoAdapter(val fillBox: Boolean = false) :
    BaseQuickAdapter<HomePhotoBean.Res.Vertical, BaseViewHolder>(R.layout.item_photo),
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
                Glide.with(itemView)
                    .load(item.thumb)
                    .placeholder(R.mipmap.ic_bg)
                    .into(photoIv)
            } else {
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