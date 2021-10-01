package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LevelListDrawable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setRoundRectBg
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 摸鱼话题列表适配器
 */
class FishPondListAdapter :
    BaseQuickAdapter<Fish.FishItem, BaseViewHolder>(R.layout.fish_pond_list_item),
    LoadMoreModule, DraggableModule {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: Fish.FishItem) {
        holder.run {
            val flAvatarContainer = getView<View>(R.id.fl_avatar_container)
            val ivAvatar = getView<ImageView>(R.id.iv_fish_pond_avatar)
            val tvNickname = getView<TextView>(R.id.tv_fish_pond_nick_name)
            val tvDesc = getView<TextView>(R.id.tv_fish_pond_desc)
            val tvContent = getView<TextView>(R.id.tv_fish_pond_content)
            val llImagesContainer = getView<ViewGroup>(R.id.ll_images_container)
            val tvLabel = getView<TextView>(R.id.tv_fish_pond_label)
            itemView.setRoundRectBg(color = Color.WHITE, cornerRadius = 10.dp)
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
            tvNickname.setTextColor(
                ContextCompat.getColor(
                    context, if (item.vip) {
                        R.color.pink
                    } else {
                        R.color.black
                    }
                )
            )
            tvNickname.text = item.nickname
            tvDesc.text =
                "${item.position} · " +
                        DateHelper.transform2FriendlyTimeSpanByNow("${item.createTime}:00")
            tvContent.text = HtmlCompat.fromHtml(
                item.content,
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
                    Glide.with(context)
                        .asBitmap()
                        .load(source)
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val bitmapDrawable = BitmapDrawable(resource)
                                drawable.addLevel(1, 1, bitmapDrawable)
                                // 判断是否为表情包
                                if (source.contains("sunofbeaches.com/emoji/") && source.endsWith(".png")) {
                                    drawable.setBounds(6, -4, textSize + 14, textSize + 4)
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
                        })
                    drawable
                },
                null
            )
            val topicName = item.topicName
            val images = item.images
            repeat(llImagesContainer.childCount) {
                // childView 只能是 ImageView 或其子类，否则会强转异常
                val imageView = llImagesContainer.getChildAt(it) as ImageView
                val imageUrl = images.getOrNull(it)
                imageView.visibility = if (imageUrl != null) {
                    // 如果是有效链接或者能获取到链接，则加载图片
                    Glide.with(itemView).load(imageUrl).into(imageView)
                    // 显示该位置的图片
                    View.VISIBLE
                } else {
                    // 隐藏该位置的图片
                    View.GONE
                }
            }
            tvLabel.visibility = if (TextUtils.isEmpty(topicName)) View.GONE else View.VISIBLE
            tvLabel.text = topicName
        }
    }
}