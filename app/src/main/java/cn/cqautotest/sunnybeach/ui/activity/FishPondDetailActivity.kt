package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.FishPondDetailActivityBinding
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.model.FishPondComment
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.adapter.ElvCommentAdapter
import cn.cqautotest.sunnybeach.ui.adapter.FishPondDetailCommendListAdapter
import cn.cqautotest.sunnybeach.util.DateHelper
import cn.cqautotest.sunnybeach.util.fromJson
import cn.cqautotest.sunnybeach.util.logByDebug
import cn.cqautotest.sunnybeach.util.toJson
import cn.cqautotest.sunnybeach.viewmodel.fishpond.FishPondViewModel
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 鱼塘详情页
 */
class FishPondDetailActivity : AppActivity() {

    private lateinit var mBinding: FishPondDetailActivityBinding
    private val mFishPondViewModel by viewModels<FishPondViewModel>()
    private val mFishPondDetailCommendListAdapter = FishPondDetailCommendListAdapter()
    private val mElvRecommendAdapter = ElvCommentAdapter()

    override fun getLayoutId(): Int = 0

    override fun onBindingView(): ViewBinding {
        mBinding = FishPondDetailActivityBinding.inflate(layoutInflater)
        return mBinding
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        val fishPond = mBinding.fishPond
        val flAvatarContainer = fishPond.flAvatarContainer
        val ivAvatar = fishPond.ivFishPondAvatar
        val tvNickname = fishPond.tvFishPondNickName
        val tvDesc = fishPond.tvFishPondDesc
        val tvContent = fishPond.tvFishPondContent
        val tvLabel = fishPond.tvFishPondLabel
        getFishItem()?.let { item ->
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
            tvDesc.text = "${item.position} · " +
                    DateHelper.transform2FriendlyTimeSpanByNow("${item.createTime}:00")
            tvContent.apply {
                maxLines = Int.MAX_VALUE
                ellipsize = null
                text = item.content
            }
            val topicName = item.topicName
            tvLabel.visibility = if (TextUtils.isEmpty(topicName)) View.GONE else View.VISIBLE
            tvLabel.text = topicName
        }
        val elvFishPondDetailComment = mBinding.elvFishPondDetailComment
        elvFishPondDetailComment.setAdapter(mElvRecommendAdapter)
    }

    override fun initData() {
        getFishItem()?.let { item ->
            val fishPondId = item.id
            mFishPondViewModel.loadFishPondRecommendListById(fishPondId)
        }
    }

    override fun initEvent() {
        val ivAvatar = mBinding.fishPond.flAvatarContainer
        val item = getFishItem()
        ivAvatar.setOnClickListener {
            ImagePreviewActivity.start(this, item?.avatar)
        }
    }

    override fun initObserver() {
        mFishPondViewModel.fishPondComment.observe(this) { fishPondRecommend ->
            // 评论列表数据
            val groupData = mutableListOf<FishPondComment.FishPondCommentItem>()
            val childData = mutableListOf<List<FishPondComment.FishPondCommentItem.SubComment>>()
            for (group in fishPondRecommend.list) {
                childData.add(group.subComments)
                groupData.add(group)
            }
            logByDebug(msg = "initData：===> groupData size is ${groupData.size}  childData size is ${childData.size}")
            mElvRecommendAdapter.setData(groupData, childData)
            val data = fishPondRecommend.list
            mFishPondDetailCommendListAdapter.setList(data)
        }
    }

    private fun getFishItem(): Fish.FishItem? {
        val fishPondByJsonText = intent.getStringExtra(IntentKey.TEXT)
        return fromJson<Fish.FishItem>(fishPondByJsonText)
    }

    override fun isStatusBarDarkFont(): Boolean = false

    companion object {

        @DebugLog
        fun start(context: Context, item: Fish.FishItem) {
            val intent = Intent(context, FishPondDetailActivity::class.java)
            intent.run {
                putExtra(IntentKey.TEXT, item.toJson())
            }
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}