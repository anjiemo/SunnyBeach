package com.example.blogsystem.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.blogsystem.R
import com.example.blogsystem.model.ArticleInfo

class ArticleAdapter :
    BaseQuickAdapter<ArticleInfo.ArticleItem, BaseViewHolder>(R.layout.item_article),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: ArticleInfo.ArticleItem) {
        holder.run {
            val ivAvatar = holder.getView<ImageView>(R.id.iv_avatar)
            val tvNickName = holder.getView<TextView>(R.id.tv_nick_name)
            val tvArticleTitle = holder.getView<TextView>(R.id.tv_article_title)
            val tvCreateTime = holder.getView<TextView>(R.id.tv_create_time)
            val tvViewCount = holder.getView<TextView>(R.id.tv_view_count)
            Glide.with(itemView).load(item.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(ivAvatar)
            tvNickName.text = item.nickName
            tvNickName.setTextColor(
                ContextCompat.getColor(
                    context, if (item.vip) {
                        R.color.pink
                    } else {
                        R.color.default_font_color
                    }
                )
            )
            tvArticleTitle.text = item.title
            tvCreateTime.text = item.createTime
            tvViewCount.text = item.viewCount.toString()
        }
    }
}

