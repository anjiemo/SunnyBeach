package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishCommendDetailActivityBinding
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.FishCommendDetailListAdapter
import cn.cqautotest.sunnybeach.util.*
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/18
 * desc   : 摸鱼评论列表页
 */
class FishCommendDetailActivity : AppActivity() {

    private lateinit var mBinding: FishCommendDetailActivityBinding
    private val mFishCommendDetailListAdapter = FishCommendDetailListAdapter()

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = FishCommendDetailActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    override fun initView() {
        mBinding.rvFishCommendDetailList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFishCommendDetailListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(1.dp))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val item: FishPondComment.FishPondCommentItem =
            fromJson(intent.getStringExtra(IntentKey.OTHER))
        val tvReplyAndGreat = mBinding.tvReplyAndGreat
        tvReplyAndGreat.text = "${item.subComments.size} 回复  |  ${item.thumbUp} 赞"
        val fishPondDetailComment = mBinding.fishPondDetailComment
        val flAvatarContainer = fishPondDetailComment.flAvatarContainer
        val ivAvatar = fishPondDetailComment.ivFishPondAvatar
        val tvNickname = fishPondDetailComment.cbFishPondNickName
        val tvDesc = fishPondDetailComment.tvFishPondDesc
        val tvReply = fishPondDetailComment.tvReplyMsg
        val tvBuildReplyMsgContainer = fishPondDetailComment.tvBuildReplyMsgContainer
        flAvatarContainer.background = if (item.vip) ContextCompat.getDrawable(
            context,
            R.drawable.avatar_circle_vip_ic
        ) else null
        Glide.with(this)
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
        tvDesc.text = "${item.position} · " +
                DateHelper.transform2FriendlyTimeSpanByNow("${item.createTime}:00")
        tvReply.text = item.content
        tvBuildReplyMsgContainer.visibility = View.GONE
        mFishCommendDetailListAdapter.setData(item)
    }

    companion object {
        fun start(context: Context, fishPondCommentItem: FishPondComment.FishPondCommentItem) {
            val intent = Intent(context, FishCommendDetailActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra(IntentKey.OTHER, fishPondCommentItem.toJson())
            context.startActivity(intent)
        }
    }
}