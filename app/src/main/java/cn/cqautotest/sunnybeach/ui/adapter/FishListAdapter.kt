package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.*
import androidx.core.app.ComponentActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FishPondListItemBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.widget.SimpleGridLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 摸鱼动态列表的适配器
 */
class FishListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<Fish.FishItem, FishListAdapter.FishListViewHolder>(FishDiffCallback()),
    SimpleGridLayout.OnNineGridClickListener {

    private val mStateList = arrayListOf<Boolean>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        addOnPagesUpdatedListener {
            mStateList.clear()
            val itemCount = snapshot().size
            mStateList.addAll(Array(itemCount) { false })
        }
    }

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

    fun setOnItemClickListener(block: (item: Fish.FishItem, position: Int) -> Unit) {
        mItemClickListener = block
    }

    inner class FishListViewHolder(val binding: FishPondListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onViewAttachedToWindow(holder: FishListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FishListAdapter.FishListViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val itemView = holder.itemView
        val binding = holder.binding
        val flAvatarContainer = binding.flAvatarContainer
        val ivAvatar = binding.ivFishPondAvatar
        val tvNickName = binding.tvFishPondNickName
        val tvDesc = binding.tvFishPondDesc
        val tvContent = binding.tvFishPondContent
        val rrlContainer = binding.rrlContainer
        val simpleGridLayout = binding.simpleGridLayout
        val tvLabel = binding.tvFishPondLabel
        val llLinkContainer = binding.llLinkContainer
        val ivLinkCover = binding.ivLinkCover
        val tvLinkTitle = binding.tvLinkTitle
        val tvLinkUrl = binding.tvLinkUrl
        val tvComment = binding.listMenuItem.tvComment
        val tvGreat = binding.listMenuItem.tvGreat
        val ivShare = binding.listMenuItem.ivShare
        val context = itemView.context
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
        val userId = item.userId
        flAvatarContainer.setFixOnClickListener {
            if (TextUtils.isEmpty(userId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(context, userId)
        }
        flAvatarContainer.background = UserManager.getAvatarPendant(item.vip)
        Glide.with(holder.itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvNickName.text = item.nickname
        tvDesc.text =
            "${item.position} · " +
                    DateHelper.getFriendlyTimeSpanByNow("${item.createTime}:00")
        tvContent.setTextIsSelectable(false)
        tvContent.text = HtmlCompat.fromHtml(
            item.content
                .replace("<br>", "\n")
                .replace("</br>", "\n"),
            HtmlCompat.FROM_HTML_MODE_LEGACY,
            { source ->
                // 1、加载本地的 emoji 表情图片（优点：理论上来说是比加载网络上的图片快的。
                // 缺点：无法做到及时更新，程序员需要重新打包apk发布更新，用户需要安装最新版本的app才可以享受最优的体验）
                // source ?: return null
                // if (source.contains("sunofbeaches.com/emoji/") && source.endsWith(".png")) {
                //     val emojiNum = Regex("\\d").find(source)?.value ?: ""
                //     val resId = context.resources.getIdentifier(
                //         "emoji_$emojiNum",
                //         "mipmap",
                //         context.packageName
                //     )
                //     val drawable = ContextCompat.getDrawable(context, resId)
                //     drawable?.let {
                //         val textSize = tvContent.textSize.toInt()
                //         it.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                //     }
                //     return drawable
                // }
                // logByDebug(msg = "covert：===> image url is $source")
                // return null

                // 2、直接获取网络的图片（优点：不需要在本地内置图片，直接使用在线图片。
                // 缺点：理论上来讲会比加载本地的图片慢一些）
                val drawable = LevelListDrawable()
                val textSize = tvContent.textSize.toInt()
                (context as? ComponentActivity)?.lifecycleScope?.launchWhenCreated {
                    val resource = DownloadHelper.ofType<Drawable>(itemView, Uri.parse(source))
                    drawable.addLevel(1, 1, resource)
                    // 判断是否为表情包
                    if (source.contains("sunofbeaches.com/emoji/") && source.endsWith(".png")) {
                        drawable.setBounds(6, 0, textSize + 6, textSize)
                    } else {
                        val drawableWidth = drawable.intrinsicWidth
                        val drawableHeight = drawable.intrinsicHeight
                        drawable.setBounds(0, 0, drawableWidth, drawableHeight)
                    }
                    drawable.level = 1
                    tvContent.invalidate()
                    tvContent.text = tvContent.text
                }
                drawable
            },
            null
        )
        tvContent.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                menu?.add(Menu.NONE, 21215, 0, "我是自定义的菜单")
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                simpleToast(item?.title ?: "")
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {

            }
        }
        val topicName = item.topicName
        val images = item.images
        val imageCount = images.size
        simpleGridLayout.setSpanCount(
            when (imageCount) {
                // 规避 0 ，避免导致：IllegalArgumentException，Span count should be at least 1. Provided 0.
                in 1..3 -> imageCount
                4 -> 2
                else -> 3
            }
        ).setOnNineGridClickListener(this)
            .setData(images)
        simpleGridLayout.isVisible = imageCount != 0
        tvLabel.isVisible = TextUtils.isEmpty(topicName).not()
        tvLabel.text = topicName
        val linkUrl = item.linkUrl
        val hasLink = TextUtils.isEmpty(linkUrl).not()
        val hasLinkCover = TextUtils.isEmpty(item.linkCover).not()
        val linkCover = if (hasLinkCover) item.linkCover
        else R.mipmap.ic_link_default
        llLinkContainer.setRoundRectBg(color = Color.parseColor("#F5F5F8"), cornerRadius = 4.dp)
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
        ivLinkCover.setRoundRectBg(cornerRadius = 3.dp)
        tvLinkTitle.text = item.linkTitle
        tvLinkUrl.text = linkUrl
        tvComment.text = with(item.commentCount) {
            if (this == 0) {
                "评论"
            } else {
                toString()
            }
        }
        tvGreat.text = with(item.thumbUpCount) {
            if (this == 0) {
                "点赞"
            } else {
                toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FishListViewHolder {
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