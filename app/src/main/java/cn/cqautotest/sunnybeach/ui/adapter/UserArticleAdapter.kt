package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.databinding.ArticleListItemBinding
import cn.cqautotest.sunnybeach.ktx.asViewBinding
import cn.cqautotest.sunnybeach.ktx.itemDiffCallback
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserArticle
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.delegate.NineGridAdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户文章列表的适配器
 */
class UserArticleAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<UserArticle.UserArticleItem, UserArticleAdapter.ArticleViewHolder>(diffCallback) {

    private val nineGridAdapterDelegate = NineGridAdapterDelegate()
    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.SIMPLIFIED_CHINESE)

    private var mMenuItemClickListener: (view: View, item: UserArticle.UserArticleItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: UserArticle.UserArticleItem, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    fun setOnNineGridClickListener(block: (sources: List<String>, index: Int) -> Unit) {
        nineGridAdapterDelegate.setOnNineGridItemClickListener(block)
    }

    inner class ArticleViewHolder(val binding: ArticleListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<ArticleListItemBinding>())

        @SuppressLint("SetTextI18n")
        fun onBinding(item: UserArticle.UserArticleItem?, position: Int) {
            item ?: return
            with(binding) {
                val ivAvatar = ivAvatar
                val tvArticleTitle = tvArticleTitle
                val tvNickName = tvNickName
                val simpleGridLayout = simpleGridLayout
                ivAvatar.loadAvatar(item.vip, item.avatar)
                tvArticleTitle.text = item.title
                tvNickName.text = "${item.nickname} · ${TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)}"
                tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
                val covers = item.covers
                val imageCount = covers.size
                simpleGridLayout.setOnNineGridClickListener(nineGridAdapterDelegate)
                    .setData(covers)
                simpleGridLayout.isVisible = imageCount != 0
                with(listMenuItem) {
                    llShare.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, position) }
                    tvComment.text = item.viewCount.toString()
                    tvGreat.text = with(item.thumbUp) {
                        if (this == 0) "点赞" else toString()
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: ArticleViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder = ArticleViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<UserArticle.UserArticleItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}