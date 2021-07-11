package cn.cqautotest.sunnybeach.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.widget.GridLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/6/18
 * desc   : 文章列表的适配器
 */
class ArticleAdapter :
    BaseQuickAdapter<ArticleInfo.ArticleItem, BaseViewHolder>(R.layout.article_item),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: ArticleInfo.ArticleItem) {
        holder.run {
            val flAvatarContainer = getView<View>(R.id.fl_avatar_container)
            val ivAvatar = getView<ImageView>(R.id.iv_avatar)
            val tvArticleTitle = getView<TextView>(R.id.tv_article_title)
            val tvNickName = getView<TextView>(R.id.tv_nick_name)
            val glPhotoList = getView<GridLayout>(R.id.gl_photo_list)
            val articleCoverOne = glPhotoList.findViewById<ImageView>(R.id.article_cover_one)
            val articleCoverTwo = glPhotoList.findViewById<ImageView>(R.id.article_cover_two)
            val articleCoverThere = glPhotoList.findViewById<ImageView>(R.id.article_cover_there)
            val tvCreateTime = getView<TextView>(R.id.tv_create_time)
            val tvViewCount = getView<TextView>(R.id.tv_view_count)
            flAvatarContainer.background = if (item.vip) ContextCompat.getDrawable(
                context,
                R.drawable.avatar_circle_vip_ic
            ) else null
            Glide.with(itemView).load(item.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(ivAvatar)
            tvArticleTitle.text = item.title
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
            val covers = item.covers
            if (covers.size > 1) {
                covers.getOrNull(0)?.run {
                    Glide.with(itemView).load(covers.getOrNull(0)).into(articleCoverOne)
                }
                covers.getOrNull(1)?.run {
                    Glide.with(itemView).load(covers.getOrNull(1)).into(articleCoverTwo)
                }
                covers.getOrNull(2)?.run {
                    Glide.with(itemView).load(covers.getOrNull(2)).into(articleCoverThere)
                }
            }
            if (covers.size == 1) {
                covers.getOrNull(0)?.run {
                    Glide.with(itemView).load(covers.getOrNull(0)).into(articleCoverTwo)
                }
            }
            glPhotoList.findViewById<ImageView>(R.id.article_cover_two)
            glPhotoList.findViewById<ImageView>(R.id.article_cover_there)
            tvCreateTime.text = item.createTime
            tvViewCount.text = item.viewCount.toString()
        }
    }
}

