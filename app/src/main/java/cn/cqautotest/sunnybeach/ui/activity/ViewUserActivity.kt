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
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ViewUserActivityBinding
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.setRoundRectBg
import cn.cqautotest.sunnybeach.ktx.takeIfLogin
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.other.FriendsStatus
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.fragment.UserMediaFragment
import cn.cqautotest.sunnybeach.util.I_LOVE_ANDROID_SITE_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_SITE_BASE_URL
import cn.cqautotest.sunnybeach.util.StringUtil
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import timber.log.Timber

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

    private fun getUserId(): String {
        val uri = intent?.data
        val scheme = uri?.scheme ?: ""
        val authority = uri?.authority ?: ""
        val lastPathSegment = uri?.lastPathSegment ?: ""
        val userId = intent.getStringExtra(IntentKey.ID) ?: ""

        Timber.d("showResult：===> scheme is $scheme authority is $authority userId is $userId lastPathSegment is $lastPathSegment")

        return when {
            checkScheme(scheme).not() -> userId
            checkAuthority(authority).not() -> userId
            checkUserId(lastPathSegment).not() -> userId
            else -> lastPathSegment
        }
    }

    /**
     * We only support http and https protocols.
     */
    private fun checkScheme(scheme: String) = scheme == "http" || scheme == "https"

    private fun checkAuthority(authority: String): Boolean {
        val sobSiteTopDomain = StringUtil.getTopDomain(SUNNY_BEACH_SITE_BASE_URL)
        val loveSiteTopDomain = StringUtil.getTopDomain(I_LOVE_ANDROID_SITE_BASE_URL)

        Timber.d("checkAuthority：===> authority is $authority")
        Timber.d("checkAuthority：===> sobSiteTopDomain is $sobSiteTopDomain")
        Timber.d("checkAuthority：===> loveSiteTopDomain is $loveSiteTopDomain")

        fun String.delete3W() = replace("www.", "")
        val sobAuthority = authority.delete3W() == sobSiteTopDomain
        val loveAuthority = authority.delete3W() == loveSiteTopDomain
        return sobAuthority || loveAuthority
    }

    /**
     * Sob site userId is long type, we need check.
     */
    private fun checkUserId(userId: String) = userId.isNotBlank() && userId.toLongOrNull() != null

    @SuppressLint("SetTextI18n")
    private fun setUpUserInfo(userId: String) {
        mUserViewModel.getUserInfo(userId).observe(this) {
            val userInfo = it.getOrNull() ?: return@observe
            mBinding.ivAvatar.loadAvatar(userInfo.vip, userInfo.avatar)
            mBinding.tvNickName.text = userInfo.nickname
            mBinding.tvNickName.setTextColor(UserManager.getNickNameColor(userInfo.vip))
            val job = if (userInfo.position.isNullOrEmpty()) "游民" else userInfo.position
            val company = if (userInfo.company.isNullOrEmpty()) "无业" else userInfo.company
            mBinding.tvDesc.text = "${job}@${company}"
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
            takeIfLogin {
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
    }

    companion object {

        @JvmStatic
        @Log
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