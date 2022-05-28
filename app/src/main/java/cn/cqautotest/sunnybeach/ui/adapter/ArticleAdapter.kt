package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
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
import cn.cqautotest.sunnybeach.model.ArticleInfo
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.delegate.NineGridAdapterDelegate
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
    PagingDataAdapter<ArticleInfo.ArticleItem, ArticleAdapter.ArticleViewHolder>(diffCallback) {

    private val nineGridAdapterDelegate = NineGridAdapterDelegate()
    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.SIMPLIFIED_CHINESE)

    private var mMenuItemClickListener: (view: View, item: ArticleInfo.ArticleItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: ArticleInfo.ArticleItem, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    fun setOnNineGridClickListener(block: (sources: List<String>, index: Int) -> Unit) {
        nineGridAdapterDelegate.setOnNineGridItemClickListener(block)
    }

    inner class ArticleViewHolder(val binding: ArticleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        constructor(parent: ViewGroup) : this(parent.asViewBinding<ArticleListItemBinding>())
    }

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
        itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
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
        simpleGridLayout.setOnNineGridClickListener(nineGridAdapterDelegate)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder = ArticleViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<ArticleInfo.ArticleItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem.id == newItem.id }
    }
}