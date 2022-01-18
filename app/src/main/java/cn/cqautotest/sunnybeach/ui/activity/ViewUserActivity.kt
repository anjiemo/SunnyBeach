package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.DebugLog
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ViewUserActivityBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.other.FriendsStatus
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.fragment.UserMediaFragment
import cn.cqautotest.sunnybeach.util.dp
import cn.cqautotest.sunnybeach.util.setFixOnClickListener
import cn.cqautotest.sunnybeach.util.setRoundRectBg
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/29
 * desc   : 查看用户信息界面
 */
class ViewUserActivity : AppActivity() {

    private val mBinding by viewBinding<ViewUserActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()
    private var mFriendsStatus = FriendsStatus.FOLLOW

    override fun getLayoutId(): Int = R.layout.view_user_activity

    override fun initView() {
        val userId = getUserId()
        setUpUserInfo(userId)
        val fragment = supportFragmentManager.findFragmentById(R.id.user_media_fragment_container)
        // 避免 Activity 重建后，重复 add Fragment 到容器里
        takeIf { fragment == null }?.let {
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.user_media_fragment_container, UserMediaFragment.newInstance(userId))
            ft.commitAllowingStateLoss()
        }
    }

    private fun getUserId() = intent.getStringExtra(IntentKey.ID) ?: ""

    @SuppressLint("SetTextI18n")
    private fun setUpUserInfo(userId: String) {
        mUserViewModel.getUserInfo(userId).observe(this) {
            val userInfo = it.getOrNull() ?: return@observe
            mBinding.flAvatarContainer.background = UserManager.getAvatarPendant(userInfo.vip)
            Glide.with(this)
                .load(userInfo.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(mBinding.ivAvatar)
            mBinding.tvNickName.text = userInfo.nickname
            mBinding.tvNickName.setTextColor(UserManager.getNickNameColor(userInfo.vip))
            mBinding.tvDesc.text = "${userInfo.position}@${userInfo.company}"
        }
        checkFollowState(userId)
        mUserViewModel.getAchievement(userId).observe(this) {
            val userAchievement = it.getOrNull() ?: return@observe
            mBinding.tvDynamicNum.text = userAchievement.momentCount.toString()
            mBinding.tvFollowNum.text = userAchievement.followCount.toString()
            mBinding.tvFansNum.text = userAchievement.fansCount.toString()
        }
    }

    private fun checkFollowState(userId: String) {
        mBinding.tvFollow.setRoundRectBg(mFriendsStatus.color, 3.dp)
        val currUserId = UserManager.loadUserBasicInfo()?.id ?: ""
        if (userId == currUserId) {
            mBinding.tvFollow.text = "编辑"
            mBinding.tvFollow.setTextColor(Color.parseColor("#1D7DFA"))
            mBinding.tvFollow.background = ContextCompat.getDrawable(this, R.drawable.edit_ic)
            return
        }
        mUserViewModel.followState(userId).observe(this) {
            val state = it.getOrNull() ?: return@observe
            mFriendsStatus = FriendsStatus.valueOfCode(state)
            mBinding.tvFollow.text = if (userId == currUserId) "编辑" else mFriendsStatus.desc
            mBinding.tvFollow.setRoundRectBg(mFriendsStatus.color, 3.dp)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        val userId = getUserId()
        mBinding.tvFollow.setFixOnClickListener {
            // 关注
            if (mFriendsStatus.isNeedFollow) {
                // 需要关注
                mUserViewModel.followUser(userId).observe(this) {
                    checkFollowState(userId)
                }
            } else {
                // 取消关注
                mUserViewModel.unfollowUser(userId).observe(this) {
                    checkFollowState(userId)
                }
            }
        }
    }

    companion object {

        @JvmStatic
        @DebugLog
        fun start(context: Context, userId: String) {
            val intent = Intent(context, ViewUserActivity::class.java)
            intent.putExtra(IntentKey.ID, userId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}