package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.databinding.FishPondDetailCommendListBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.model.UserComment
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/18
 * desc   : 摸鱼评论列表适配器
 */
class FishCommendDetailListAdapter : RecyclerView.Adapter<FishDetailCommendListViewHolder>() {

    private lateinit var mData: FishPondComment.FishPondCommentItem

    private var mCommentClickListener: (item: UserComment, position: Int) -> Unit =
        { _, _ -> }

    fun setOnCommentClickListener(block: (item: UserComment, position: Int) -> Unit) {
        mCommentClickListener = block
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: FishPondComment.FishPondCommentItem) {
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FishDetailCommendListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FishPondDetailCommendListBinding.inflate(inflater, parent, false)
        return FishDetailCommendListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FishDetailCommendListViewHolder, position: Int) {
        val item = mData.subComments[position]
        val itemView = holder.itemView
        val binding = holder.binding
        val flAvatarContainer = binding.flAvatarContainer
        val ivAvatar = binding.ivFishPondAvatar
        val tvNickName = binding.cbFishPondNickName
        val ivPondComment = binding.ivFishPondComment
        val tvDesc = binding.tvFishPondDesc
        val tvReply = binding.tvReplyMsg
        val tvBuildReplyMsgContainer = binding.tvBuildReplyMsgContainer
        val context = itemView.context
        val userId = item.getUserId()
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
        tvNickName.text = item.getNickName()
        ivPondComment.setFixOnClickListener {
            mCommentClickListener.invoke(item, position)
        }
        // 摸鱼详情列表的时间没有精确到秒
        tvDesc.text = "${item.position} · " +
                DateHelper.getFriendlyTimeSpanByNow("${item.createTime}:00")
        tvReply.text = getBeautifiedFormat(item, mData)
        tvBuildReplyMsgContainer.isVisible = false
    }

    private fun getBeautifiedFormat(
        subComment: FishPondComment.FishPondCommentItem.SubComment,
        item: FishPondComment.FishPondCommentItem
    ): Spanned {
        val whoReplied = ""
        val wasReplied = subComment.getTargetUserNickname()
        val content = whoReplied + "回复" + wasReplied + "：" + subComment.content
        val spannableString = SpannableString(content)
        val color = Color.parseColor("#045FB2")
        spannableString.setSpan(
            ForegroundColorSpan(color),
            content.indexOf(whoReplied),
            content.indexOf("回复"),
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color),
            content.indexOf(wasReplied),
            content.indexOf(wasReplied) + wasReplied.length,
            SpannableString.SPAN_INCLUSIVE_INCLUSIVE
        )
        return spannableString
    }

    override fun getItemCount(): Int = mData.subComments.size
}