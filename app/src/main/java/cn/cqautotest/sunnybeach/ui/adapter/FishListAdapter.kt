package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.*
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FishPondListItemBinding
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.DownloadHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import cn.cqautotest.sunnybeach.util.simpleToast
import com.bumptech.glide.Glide


/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/9/6
 * desc   : 摸鱼动态列表的适配器
 */
class FishListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<Fish.FishItem, FishListAdapter.FishListViewHolder>(object :
        DiffUtil.ItemCallback<Fish.FishItem>() {
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
    }) {

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
        val tvNickname = binding.tvFishPondNickName
        val tvDesc = binding.tvFishPondDesc
        val tvContent = binding.tvFishPondContent
        val rrlContainer = binding.rrlContainer
        val simpleGridLayout = binding.simpleGridLayout
        val tvLabel = binding.tvFishPondLabel
        val tvComment = binding.listMenuItem.tvComment
        val tvGreat = binding.listMenuItem.tvGreat
        val ivShare = binding.listMenuItem.ivShare
        val context = itemView.context
        itemView.setFixOnClickListener {
            mItemClickListener.invoke(item, position)
        }
        flAvatarContainer.background = if (item.vip) ContextCompat.getDrawable(
            context,
            R.drawable.avatar_circle_vip_ic
        ) else null
        Glide.with(holder.itemView)
            .load(item.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(ivAvatar)
        val nickNameColor = if (item.vip) {
            R.color.pink
        } else {
            R.color.black
        }
        tvNickname.setTextColor(ContextCompat.getColor(context, nickNameColor))
        tvNickname.text = item.nickname
        tvDesc.text =
            "${item.position} · " +
                    DateHelper.transform2FriendlyTimeSpanByNow("${item.createTime}:00")
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
                    val resource =
                        DownloadHelper.getTypeByUri<Drawable>(itemView, Uri.parse(source))
                    drawable.addLevel(1, 1, resource)
                    // 判断是否为表情包
                    if (source.contains("sunofbeaches.com/emoji/") && source.endsWith(".png")) {
                        drawable.setBounds(6, 0, textSize + 6, textSize)
                    } else {
                        drawable.setBounds(
                            0,
                            0,
                            drawable.intrinsicWidth,
                            drawable.intrinsicHeight
                        )
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
                else -> 2
            }
        ).setData(images)
        tvLabel.visibility = if (TextUtils.isEmpty(topicName)) View.GONE else View.VISIBLE
        tvLabel.text = topicName
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
}