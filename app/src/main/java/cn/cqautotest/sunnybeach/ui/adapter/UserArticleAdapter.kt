package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
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
import cn.cqautotest.sunnybeach.model.ArticleSearchFilter
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
    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

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

                val articleState = ArticleSearchFilter.ArticleState.valueOfSate(item.state)
                val stateName = when (articleState) {
                    ArticleSearchFilter.ArticleState.ALL -> ""
                    ArticleSearchFilter.ArticleState.PUBLISHED -> ArticleSearchFilter.ArticleState.PUBLISHED.typeName
                    ArticleSearchFilter.ArticleState.DRAFT -> ArticleSearchFilter.ArticleState.DRAFT.typeName
                    ArticleSearchFilter.ArticleState.UNDER_REVIEW -> ArticleSearchFilter.ArticleState.UNDER_REVIEW.typeName
                    ArticleSearchFilter.ArticleState.FAILED -> ArticleSearchFilter.ArticleState.FAILED.typeName
                }
                stvState.isVisible = true
                stvState.setText(stateName)
                stvState.setTextColor(getTextColorByState(articleState))
                stvState.setColorBackground(getBgColorByState(articleState))
                simpleGridLayout.setData(item.covers)
                    .setOnNineGridClickListener(nineGridAdapterDelegate)
                with(listMenuItem) {
                    llShare.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, position) }
                    tvComment.text = item.viewCount.toString()
                    tvGreat.text = with(item.thumbUp) {
                        if (this == 0) "点赞" else toString()
                    }
                }
            }
        }

        private fun getBgColorByState(articleState: ArticleSearchFilter.ArticleState): Int {
            return when (articleState) {
                ArticleSearchFilter.ArticleState.ALL -> Color.TRANSPARENT
                ArticleSearchFilter.ArticleState.PUBLISHED -> Color.parseColor("#F0F9EB")
                ArticleSearchFilter.ArticleState.UNDER_REVIEW -> Color.parseColor("#FDF6EC")
                ArticleSearchFilter.ArticleState.DRAFT -> Color.parseColor("#F4F4F5")
                ArticleSearchFilter.ArticleState.FAILED -> Color.parseColor("#F56C6C")
            }
        }

        private fun getTextColorByState(articleState: ArticleSearchFilter.ArticleState): Int {
            return when (articleState) {
                ArticleSearchFilter.ArticleState.ALL -> Color.TRANSPARENT
                ArticleSearchFilter.ArticleState.PUBLISHED -> Color.parseColor("#67C23A")
                ArticleSearchFilter.ArticleState.UNDER_REVIEW -> Color.parseColor("#E6A23C")
                ArticleSearchFilter.ArticleState.DRAFT -> Color.parseColor("#909399")
                ArticleSearchFilter.ArticleState.FAILED -> Color.WHITE
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