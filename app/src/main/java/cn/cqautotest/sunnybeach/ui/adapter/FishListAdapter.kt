package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FishPondListItemBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.ifNullOrEmpty
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.EmojiImageGetter
import cn.cqautotest.sunnybeach.widget.SimpleGridLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 摸鱼动态列表的适配器
 */
class FishListAdapter(private val adapterDelegate: AdapterDelegate, private val expandContent: Boolean = false) :
    PagingDataAdapter<Fish.FishItem, FishListAdapter.FishListViewHolder>(FishDiffCallback()),
    SimpleGridLayout.OnNineGridClickListener {

    class FishDiffCallback : DiffUtil.ItemCallback<Fish.FishItem>() {
        override fun areItemsTheSame(
            oldItem: Fish.FishItem,
            newItem: Fish.FishItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Fish.FishItem,
            newItem: Fish.FishItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private var mItemClickListener: (item: Fish.FishItem, position: Int) -> Unit = { _, _ -> }
    private var mMenuItemClickListener: (view: View, item: Fish.FishItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnItemClickListener(block: (item: Fish.FishItem, position: Int) -> Unit) {
        mItemClickListener = block
    }

    fun setOnMenuItemClickListener(block: (view: View, item: Fish.FishItem, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    inner class FishListViewHolder(val binding: FishPondListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: FishListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FishListAdapter.FishListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val ivAvatar = binding.ivFishPondAvatar
        val tvNickName = binding.tvFishPondNickName
        val tvDesc = binding.tvFishPondDesc
        val tvContent = binding.tvFishPondContent
        val simpleGridLayout = binding.simpleGridLayout
        val tvLabel = binding.tvFishPondLabel
        val llLinkContainer = binding.llLinkContainer
        val ivLinkCover = binding.ivLinkCover
        val tvLinkTitle = binding.tvLinkTitle
        val tvLinkUrl = binding.tvLinkUrl
        val tvComment = binding.listMenuItem.tvComment
        val llGreat = binding.listMenuItem.llGreat
        val ivGreat = binding.listMenuItem.ivGreat
        val tvGreat = binding.listMenuItem.tvGreat
        val llShare = binding.listMenuItem.llShare
        val context = itemView.context
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
        llShare.setFixOnClickListener {
            mMenuItemClickListener.invoke(it, item, position)
        }
        llGreat.setFixOnClickListener {
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
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvNickName.text = item.nickname
        val job = item.position.ifNullOrEmpty { "游民" }
        tvDesc.text = "$job · " + DateHelper.getFriendlyTimeSpanByNow("${item.createTime}:00")
        tvContent.setTextIsSelectable(false)
        tvContent.apply {
            if (expandContent) {
                maxLines = Int.MAX_VALUE
                ellipsize = null
            } else {
                maxLines = 5
                ellipsize = TextUtils.TruncateAt.END
            }
        }
        val content = item.content
        // 设置默认表情符号解析器
        tvContent.setDefaultEmojiParser()
        tvContent.text = content.parseAsHtml(imageGetter = EmojiImageGetter(tvContent.textSize.toInt()))
        val topicName = item.topicName
        val images = item.images
        val imageCount = images.size
        simpleGridLayout.setOnNineGridClickListener(this)
            .setData(images)
        simpleGridLayout.isVisible = imageCount != 0
        tvLabel.isVisible = TextUtils.isEmpty(topicName).not()
        tvLabel.text = topicName
        val linkUrl = item.linkUrl
        val hasLink = TextUtils.isEmpty(linkUrl).not()
        val hasLinkCover = TextUtils.isEmpty(item.linkCover).not()
        val linkCover = if (hasLinkCover) item.linkCover
        else R.mipmap.ic_link_default
        llLinkContainer.isVisible = hasLink
        llLinkContainer.setFixOnClickListener {
            BrowserActivity.start(context, linkUrl)
        }
        Glide.with(context)
            .load(linkCover)
            .placeholder(R.mipmap.ic_link_default)
            .error(R.mipmap.ic_link_default)
            .transform(RoundedCorners(3.dp))
            .into(ivLinkCover)
        tvLinkTitle.text = item.linkTitle
        tvLinkUrl.text = linkUrl
        tvComment.text = with(item.commentCount) {
            if (this == 0) {
                "评论"
            } else {
                toString()
            }
        }
        tvGreat.text = with(item.thumbUpList.size) {
            if (this == 0) {
                "点赞"
            } else {
                toString()
            }
        }
        val currUserId = UserManager.loadCurrUserId()
        val like = item.thumbUpList.contains(currUserId)
        val defaultColor = ContextCompat.getColor(context, R.color.menu_default_font_color)
        val likeColor = ContextCompat.getColor(context, R.color.menu_like_font_color)
        ivGreat.imageTintList = ColorStateList.valueOf((if (like) likeColor else defaultColor))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FishPondListItemBinding.inflate(inflater, parent, false)
        return FishListViewHolder(binding)
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
}