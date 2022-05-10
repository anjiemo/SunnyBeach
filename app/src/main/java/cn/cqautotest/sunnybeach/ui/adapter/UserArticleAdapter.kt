package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.ArticleListItemBinding
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.widget.SimpleGridLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户文章列表的适配器
 */
class UserArticleAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserArticle.UserArticleItem, UserArticleAdapter.ArticleViewHolder>(
        UserArticleDiffCallback()
    ), SimpleGridLayout.OnNineGridClickListener {

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

    private var mMenuItemClickListener: (view: View, item: UserArticle.UserArticleItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: UserArticle.UserArticleItem, position: Int) -> Unit) {
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val itemView = holder.itemView
        val binding = holder.binding
        val ivAvatar = binding.ivAvatar
        val tvArticleTitle = binding.tvArticleTitle
        val tvNickName = binding.tvNickName
        val rrlContainer = binding.rrlContainer
        val simpleGridLayout = binding.simpleGridLayout
        val tvViewCount = binding.listMenuItem.tvComment
        val tvGreat = binding.listMenuItem.tvGreat
        val llShare = binding.listMenuItem.llShare
        val item = getItem(position) ?: return
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        llShare.setFixOnClickListener {
            mMenuItemClickListener.invoke(it, item, position)
        }
        ivAvatar.loadAvatar(item.vip, item.avatar)
        tvArticleTitle.text = item.title
        tvNickName.text =
            "${item.nickname} · ${DateHelper.getFriendlyTimeSpanByNow(item.createTime)}"
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        val covers = item.covers
        val imageCount = covers.size
        simpleGridLayout.setOnNineGridClickListener(this)
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