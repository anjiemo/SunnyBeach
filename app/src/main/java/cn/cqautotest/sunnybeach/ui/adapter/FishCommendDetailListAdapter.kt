package cn.cqautotest.sunnybeach.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spanned
import android.text.TextUtils
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import cn.cqautotest.sunnybeach.ktx.ifNullOrEmpty
import cn.cqautotest.sunnybeach.ktx.setDefaultEmojiParser
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.model.UserComment
import cn.cqautotest.sunnybeach.ui.activity.ViewUserActivity
import com.blankj.utilcode.util.TimeUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/18
 * desc   : 摸鱼评论列表适配器
 */
class FishCommendDetailListAdapter : RecyclerView.Adapter<FishDetailCommendListViewHolder>() {

    private lateinit var mData: FishPondComment.FishPondCommentItem

    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    private var mCommentClickListener: (item: UserComment, position: Int) -> Unit = { _, _ -> }

    fun setOnCommentClickListener(block: (item: UserComment, position: Int) -> Unit) {
        mCommentClickListener = block
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: FishPondComment.FishPondCommentItem) {
        mData = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishDetailCommendListViewHolder =
        FishDetailCommendListViewHolder(parent)

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FishDetailCommendListViewHolder, position: Int) {
        val item = mData.subComments[position]
        val itemView = holder.itemView
        val binding = holder.binding
        val ivAvatar = binding.ivFishPondAvatar
        val tvNickName = binding.cbFishPondNickName
        val ivPondComment = binding.ivFishPondComment
        val tvDesc = binding.tvFishPondDesc
        val tvReply = binding.tvReplyMsg
        val tvBuildReplyMsgContainer = binding.tvBuildReplyMsgContainer
        val context = itemView.context
        val userId = item.getUserId()
        ivAvatar.setFixOnClickListener {
            if (TextUtils.isEmpty(userId)) {
                return@setFixOnClickListener
            }
            ViewUserActivity.start(context, userId)
        }
        ivAvatar.loadAvatar(item.vip, item.avatar)
        tvNickName.setTextColor(UserManager.getNickNameColor(item.vip))
        tvNickName.text = item.getNickName()
        ivPondComment.setFixOnClickListener {
            mCommentClickListener.invoke(item, position)
        }
        val job = item.position.ifNullOrEmpty { "滩友" }
        // 摸鱼详情列表的时间没有精确到秒
        tvDesc.text = "$job · " + TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
        tvReply.setDefaultEmojiParser()
        tvReply.text = getBeautifiedFormat(item, mData)
        tvBuildReplyMsgContainer.isVisible = false
    }

    private fun getBeautifiedFormat(
        subComment: FishPondComment.FishPondCommentItem.SubComment, item: FishPondComment.FishPondCommentItem
    ): Spanned {
        val whoReplied = ""
        val wasReplied = subComment.getTargetUserNickname()
        val color = Color.parseColor("#045FB2")
        return buildSpannedString {
            color(color) { append(whoReplied) }
            append("回复 ")
            color(color) { append(wasReplied) }
            append(" ：")
            append(subComment.content)
        }
    }

    override fun getItemCount(): Int = mData.subComments.size
}