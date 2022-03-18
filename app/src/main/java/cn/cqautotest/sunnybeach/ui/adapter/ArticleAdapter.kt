package cn.cqautotest.sunnybeach.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.ArticleListItemBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import cn.cqautotest.sunnybeach.widget.SimpleGridLayout
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 文章列表的适配器
 */
class ArticleAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<ArticleInfo.ArticleItem, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()),
    SimpleGridLayout.OnNineGridClickListener {

    class ArticleDiffCallback : DiffUtil.ItemCallback<ArticleInfo.ArticleItem>() {
        override fun areItemsTheSame(
            oldItem: ArticleInfo.ArticleItem,
            newItem: ArticleInfo.ArticleItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ArticleInfo.ArticleItem,
            newItem: ArticleInfo.ArticleItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var mMenuItemClickListener: (view: View, item: ArticleInfo.ArticleItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: ArticleInfo.ArticleItem, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    private lateinit var mOnNineGridClickListener: SimpleGridLayout.OnNineGridClickListener

    fun setOnNineGridClickListener(listener: SimpleGridLayout.OnNineGridClickListener) {
        mOnNineGridClickListener = listener
    }

    fun setOnNineGridClickListener(block: (sources: List<String>, index: Int) -> Unit) {
        mOnNineGridClickListener = object : SimpleGridLayout.OnNineGridClickListener {
            override fun onNineGridClick(sources: List<String>, index: Int) {
                block.invoke(sources, index)
            }
        }
    }

    override fun onNineGridClick(sources: List<String>, index: Int) {
        if (::mOnNineGridClickListener.isInitialized) {
            mOnNineGridClickListener.onNineGridClick(sources, index)
        }
    }

    inner class ArticleViewHolder(val binding: ArticleListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: ArticleViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val itemView = holder.itemView
        val binding = holder.binding
        val flAvatarContainer = binding.flAvatarContainer
        val ivAvatar = binding.ivAvatar
        val tvArticleTitle = binding.tvArticleTitle
        val tvNickName = binding.tvNickName
        val rrlContainer = binding.rrlContainer
        val simpleGridLayout = binding.simpleGridLayout
        val tvViewCount = binding.listMenuItem.tvComment
        val tvGreat = binding.listMenuItem.tvGreat
        val llShare = binding.listMenuItem.llShare
        val llGreat = binding.listMenuItem.llGreat
        val ivShare = binding.listMenuItem.ivShare
        val context = itemView.context
        val item = getItem(position) ?: return
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        llShare.setFixOnClickListener {
            mMenuItemClickListener.invoke(it, item, position)
        }
        flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        tvArticleTitle.text = item.title
        tvNickName.text =
            "${item.nickName} · ${DateHelper.getFriendlyTimeSpanByNow(item.createTime)}"
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        val covers = item.covers
        val imageCount = covers.size
        simpleGridLayout.setSpanCount(
            when (imageCount) {
                // 规避 0 ，避免导致：IllegalArgumentException，Span count should be at least 1. Provided 0.
                in 1..3 -> imageCount
                4 -> 2
                else -> 3
            }
        ).setOnNineGridClickListener(this)
            .setData(covers)
        rrlContainer.isVisible = imageCount != 0
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