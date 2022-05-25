package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
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
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.widget.SimpleGridLayout
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

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

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.SIMPLIFIED_CHINESE)

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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val itemView = holder.itemView
        val binding = holder.binding
        val ivAvatar = binding.ivAvatar
        val tvArticleTitle = binding.tvArticleTitle
        val tvNickName = binding.tvNickName
        val simpleGridLayout = binding.simpleGridLayout
        val tvViewCount = binding.listMenuItem.tvComment
        val tvGreat = binding.listMenuItem.tvGreat
        val llShare = binding.listMenuItem.llShare
        val context = itemView.context
        val item = getItem(position) ?: return
        itemView.setFixOnClickListener {
            adapterDelegate.onItemClick(it, position)
        }
        llShare.setFixOnClickListener {
            mMenuItemClickListener.invoke(it, item, position)
        }
        val userId = item.userId
        ivAvatar.setFixOnClickListener {
            if (TextUtils.isEmpty(userId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(context, userId)
        }
        ivAvatar.loadAvatar(item.vip, item.avatar)
        tvArticleTitle.text = item.title
        tvNickName.text = "${item.nickName} · ${TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)}"
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        val covers = item.covers
        val imageCount = covers.size
        simpleGridLayout.setOnNineGridClickListener(this)
            .setData(covers)
        simpleGridLayout.isVisible = imageCount != 0
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