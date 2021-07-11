package cn.cqautotest.sunnybeach.ui.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.databinding.FishPondDetailActivityBinding
import cn.cqautotest.sunnybeach.model.Fish
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.utils.fromJson
import cn.cqautotest.sunnybeach.utils.toJson
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/7/11
 * desc   : 鱼塘详情页
 */
class FishPondDetailActivity : AppActivity() {

    private lateinit var mBinding: FishPondDetailActivityBinding

    override fun getLayoutId(): Int = R.layout.fish_pond_detail_activity

    override fun onBindingView() {
        mBinding = FishPondDetailActivityBinding.bind(viewBindingRoot)
    }

    override fun initView() {
        val fishPond = mBinding.fishPond
        val flAvatarContainer = fishPond.flAvatarContainer
        val ivAvatar = fishPond.tvFishPondAvatar
        val tvNickname = fishPond.tvFishPondNickName
        val tvDesc = fishPond.tvFishPondDesc
        val tvContent = fishPond.tvFishPondContent
        val tvLabel = fishPond.tvFishPondLabel
        val fishPondByJsonText = intent.getStringExtra(IntentKey.TEXT)
        val item = fromJson<Fish.FishItem>(fishPondByJsonText)
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
        tvDesc.text = item.position
        tvContent.apply {
            maxLines = Int.MAX_VALUE
            ellipsize = null
            text = item.content
        }
        val topicName = item.topicName
        tvLabel.visibility = if (TextUtils.isEmpty(topicName)) View.GONE else View.VISIBLE
        tvLabel.text = topicName
    }

    override fun initData() {

    }

    companion object {

        @JvmStatic
        @DebugLog
        fun start(item: Fish.FishItem) {
            val context = AppApplication.getInstance().applicationContext
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