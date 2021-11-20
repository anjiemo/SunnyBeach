package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.ArticleListItemBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.dp
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户文章列表的适配器
 */
class UserArticleAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserArticle.UserArticleItem, UserArticleAdapter.ArticleViewHolder>(
        UserArticleDiffCallback()
    ) {

    class UserArticleDiffCallback : DiffUtil.ItemCallback<UserArticle.UserArticleItem>() {
        override fun areItemsTheSame(
            oldItem: UserArticle.UserArticleItem,
            newItem: UserArticle.UserArticleItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UserArticle.UserArticleItem,
            newItem: UserArticle.UserArticleItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class ArticleViewHolder(val binding: ArticleListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: ArticleViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val itemView = holder.itemView
        val binding = holder.binding
        val flAvatarContainer = binding.flAvatarContainer
        val ivAvatar = binding.ivAvatar
        val tvArticleTitle = binding.tvArticleTitle
        val tvNickName = binding.tvNickName
        val rrlContainer = binding.rrlContainer
        val llImagesContainer = binding.llImagesContainer
        val tvViewCount = binding.listMenuItem.tvComment
        val tvGreat = binding.listMenuItem.tvGreat
        val ivShare = binding.listMenuItem.ivShare
        val context = itemView.context
        val item = getItem(position) ?: return
        flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        tvArticleTitle.text = item.title
        tvNickName.text =
            "${item.nickname} · ${DateHelper.getFriendlyTimeSpanByNow(item.createTime)}"
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        val covers = item.covers
        rrlContainer.isVisible = covers.isNotEmpty()
        llImagesContainer.layoutParams = RelativeLayout.LayoutParams(
            (ScreenUtils.getScreenWidth() - 40.dp) / 3 * covers.size,
            (ScreenUtils.getScreenWidth() - 40.dp) / 3
        )
        repeat(llImagesContainer.childCount) {
            // childView 只能是 ImageView 或其子类，否则会强转异常
            val imageView = llImagesContainer.getChildAt(it) as ImageView
            val imageUrl = covers.getOrNull(it)
            imageView.layoutParams = LinearLayout.LayoutParams(
                (ScreenUtils.getScreenWidth() - 40.dp) / 3,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            imageView.isVisible = if (imageUrl != null) {
                // 如果是有效链接或者能获取到链接，则加载图片
                Glide.with(itemView).load(imageUrl).into(imageView)
                // 显示该位置的图片
                true
            } else {
                // 隐藏该位置的图片
                false
            }
        }
        // tvCreateTime.text = item.createTime
        tvViewCount.text = item.viewCount.toString()
        tvGreat.text = with(item.thumbUp) {
            if (this == 0) {
                "点赞"
            } else {
                toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleListItemBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding)
    }
}