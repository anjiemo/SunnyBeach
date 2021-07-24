package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.model.FishPondComment
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 摸鱼话题评论列表适配器
 */
class FishPondDetailCommendListAdapter :
    BaseQuickAdapter<FishPondComment.FishPondCommentItem, BaseViewHolder>(
        R.layout.fish_pond_detail_commend_list
    ),
    LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: FishPondComment.FishPondCommentItem) {
        holder.run {
            val flAvatarContainer = getView<View>(R.id.fl_avatar_container)
            val ivAvatar = getView<ImageView>(R.id.iv_fish_pond_avatar)
            val tvNickname = getView<TextView>(R.id.tv_fish_pond_nick_name)
            val tvDesc = getView<TextView>(R.id.tv_fish_pond_desc)
            val tvReply = getView<TextView>(R.id.tv_reply_msg)
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
            // 摸鱼详情列表的时间没有精确到秒
            tvDesc.text = item.position
            tvReply.text = item.content
        }
    }
}