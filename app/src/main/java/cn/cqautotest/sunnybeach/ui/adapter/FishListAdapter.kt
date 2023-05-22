package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FishPondListItemBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.adapter.delegate.NineGridAdapterDelegate
import cn.cqautotest.sunnybeach.util.EmojiImageGetter
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 摸鱼动态列表的适配器
 */
class FishListAdapter(private val adapterDelegate: AdapterDelegate, private val expandContent: Boolean = false) :
    PagingDataAdapter<Fish.FishItem, FishListAdapter.FishListViewHolder>(diffCallback) {

    private val nineGridAdapterDelegate = NineGridAdapterDelegate()
    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    private var mMenuItemClickListener: (view: View, item: Fish.FishItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: Fish.FishItem, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    inner class FishListViewHolder(val binding: FishPondListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<FishPondListItemBinding>())

        @SuppressLint("SetTextI18n")
        fun onBinding(item: Fish.FishItem?, position: Int) {
            item ?: return
            with(binding) {
                val userId = item.userId
                ivFishPondAvatar.setFixOnClickListener { takeIf { userId.isNotEmpty() }?.let { ViewUserActivity.start(context, userId) } }
                ivFishPondAvatar.loadAvatar(item.vip, item.avatar)
                tvFishPondNickName.setTextColor(UserManager.getNickNameColor(item.vip))
                tvFishPondNickName.text = item.nickname
                val job = item.position.ifNullOrEmpty { "滩友" }
                tvFishPondDesc.text = "$job · " + TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
                tvFishPondContent.setTextIsSelectable(false)
                tvFishPondContent.apply {
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
                tvFishPondContent.setDefaultEmojiParser()
                tvFishPondContent.text = content.parseAsHtml(imageGetter = EmojiImageGetter(tvFishPondContent.textSize.toInt()))
                val topicName = item.topicName
                val images = item.images
                val imageCount = images.size
                simpleGridLayout.setOnNineGridClickListener(nineGridAdapterDelegate)
                    .setData(images)
                llPhotoContainer.isVisible = imageCount != 0
                tvFishPondLabel.isVisible = TextUtils.isEmpty(topicName).not()
                tvFishPondLabel.text = topicName
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
                val currUserId = UserManager.loadCurrUserId()
                val like = currUserId in item.thumbUpList
                val defaultColor = ContextCompat.getColor(context, R.color.menu_default_font_color)
                val likeColor = ContextCompat.getColor(context, R.color.menu_like_font_color)
                with(listMenuItem) {
                    llShare.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, bindingAdapterPosition) }
                    llGreat.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, bindingAdapterPosition) }
                    ivGreat.imageTintList = ColorStateList.valueOf((if (like) likeColor else defaultColor))
                    tvComment.text = with(item.commentCount) {
                        if (this == 0) {
                            "评论"
                        } else {
                            toString()
                        }
                    }
                    tvGreat.text = with(item.thumbUpList.size) {
                        if (this == 0) "点赞" else toString()
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: FishListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: FishListAdapter.FishListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishListViewHolder = FishListViewHolder(parent)

    fun setOnNineGridClickListener(block: (sources: List<String>, index: Int) -> Unit) {
        nineGridAdapterDelegate.setOnNineGridItemClickListener(block)
    }

    companion object {

        private val diffCallback =
            itemDiffCallback<Fish.FishItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}