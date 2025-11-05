package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.aop.Log
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.ViewUserActivityBinding
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.ifLoginThen
import cn.cqautotest.sunnybeach.ktx.ifNullOrEmpty
import cn.cqautotest.sunnybeach.ktx.orEmpty
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.setRoundRectBg
import cn.cqautotest.sunnybeach.ktx.waitViewDrawFinished
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.ReportType
import cn.cqautotest.sunnybeach.model.UserInfo
import cn.cqautotest.sunnybeach.other.FriendsStatus
import cn.cqautotest.sunnybeach.other.IntentKey
import cn.cqautotest.sunnybeach.ui.dialog.ShareDialog
import cn.cqautotest.sunnybeach.ui.fragment.UserMediaFragment
import cn.cqautotest.sunnybeach.ui.popup.ActionClickType
import cn.cqautotest.sunnybeach.ui.popup.UserMoreActionPopup
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_VIEW_USER_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.dylanc.longan.lifecycleOwner
import com.hjq.bar.TitleBar
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BubbleAttachPopupView
import com.umeng.socialize.media.UMWeb
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/29
 * desc   : 查看用户信息界面
 */
@AndroidEntryPoint
class ViewUserActivity : AppActivity() {

    private val mBinding by viewBinding(ViewUserActivityBinding::bind)
    private val mUserViewModel by viewModels<UserViewModel>()
    private var mFriendsStatus = FriendsStatus.FOLLOW
    private var mUserInfo: UserInfo? = null

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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                mBinding.userMediaFragmentContainer.waitViewDrawFinished()
                mBinding.userMediaFragmentContainer.apply {
                    updateLayoutParams {
                        height = mBinding.root.bottom - mBinding.titleBar.bottom
                    }
                }
            }
        }
    }

    private fun getUserId(): String {
        val uri = intent?.data
        val scheme = uri?.scheme.orEmpty()
        val authority = uri?.authority.orEmpty()
        val lastPathSegment = uri?.lastPathSegment.orEmpty()
        val userId = intent.getStringExtra(IntentKey.ID).orEmpty()

        Timber.d("showResult：===> scheme is $scheme authority is $authority userId is $userId lastPathSegment is $lastPathSegment")

        return when {
            mUserViewModel.checkScheme(scheme).not() -> userId
            mUserViewModel.checkAuthority(authority).not() -> userId
            mUserViewModel.checkUserId(lastPathSegment).not() -> userId
            else -> lastPathSegment
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUserInfo(userId: String) {
        with(mBinding) {
            mUserViewModel.getUserInfo(userId).observe(lifecycleOwner) {
                val userInfo = it.getOrNull() ?: return@observe
                mUserInfo = userInfo
                ivAvatar.setFixOnClickListener { ImagePreviewActivity.start(context, userInfo.avatar) }
                ivAvatar.loadAvatar(userInfo.vip, userInfo.avatar)
                tvNickName.text = userInfo.nickname
                tvNickName.setTextColor(UserManager.getNickNameColor(userInfo.vip))
                val job = userInfo.position.ifNullOrEmpty { "滩友" }
                val company = userInfo.company.ifNullOrEmpty { "无业" }
                tvDesc.text = "${job}@${company}"
            }
            checkFollowState(userId)
            mUserViewModel.getAchievement(userId).observe(lifecycleOwner) {
                val userAchievement = it.getOrNull() ?: return@observe
                tvDynamicNum.text = userAchievement.momentCount.toString()
                tvFollowNum.text = userAchievement.followCount.toString()
                tvFansNum.text = userAchievement.fansCount.toString()
            }
        }
    }

    private fun checkFollowState(userId: String) {
        with(mBinding) {
            tvFollow.setRoundRectBg(mFriendsStatus.color, 3.dp)
            val currUserId = UserManager.loadUserBasicInfo()?.id.orEmpty()
            if (userId == currUserId) {
                tvFollow.text = "编辑"
                tvFollow.setTextColor("#1D7DFA".toColorInt())
                tvFollow.background = ContextCompat.getDrawable(context, R.drawable.edit_ic)
                return
            }
            mUserViewModel.followState(userId).observe(lifecycleOwner) {
                val state = it.getOrNull() ?: return@observe
                mFriendsStatus = FriendsStatus.valueOfCode(state)
                tvFollow.text = mFriendsStatus.desc
                tvFollow.setRoundRectBg(mFriendsStatus.color, 3.dp)
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        val userId = getUserId()
        with(mBinding) {
            tvFollow.setFixOnClickListener {
                ifLoginThen {
                    // 关注
                    if (mFriendsStatus.isNeedFollow) {
                        // 需要关注
                        mUserViewModel.followUser(userId).observe(lifecycleOwner) {
                            checkFollowState(userId)
                        }
                    } else {
                        // 取消关注
                        mUserViewModel.unfollowUser(userId).observe(lifecycleOwner) {
                            checkFollowState(userId)
                        }
                    }
                }
            }
        }
    }

    override fun onRightClick(titleBar: TitleBar) {
        val userId = mUserInfo?.userId ?: return
        XPopup.Builder(this)
            .dismissOnBackPressed(true)
            .dismissOnTouchOutside(true)
            .isDestroyOnDismiss(true)
            .atView(titleBar.rightView)
            .offsetX((-6).dp)
            .offsetY((-16).dp)
            .asCustom(object : BubbleAttachPopupView(this), SavedStateRegistryOwner {

                private val mSavedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)

                init {
                    initLifecycle()
                }

                /**
                 * See：[androidx.fragment.app.Fragment.initLifecycle]
                 */
                private fun initLifecycle() {
                    setViewTreeLifecycleOwner(this)
                    setViewTreeSavedStateRegistryOwner(this)
                    mSavedStateRegistryController.performAttach()
                    mSavedStateRegistryController.performRestore(bundleOf())
                }

                override val savedStateRegistry: SavedStateRegistry
                    get() = mSavedStateRegistryController.savedStateRegistry

                override fun getImplLayoutId(): Int = R.layout.popup_user_more_action

                override fun onCreate() {
                    super.onCreate()
                    setBubbleBgColor(Color.WHITE)
                    setArrowWidth(8.dp)
                    setArrowHeight(9.dp)
                    setArrowRadius(2.dp)
                    findViewById<ComposeView>(R.id.compose_view)
                        .also { it.setViewCompositionStrategy(ViewCompositionStrategy.Default) }
                        .setContent {
                            UserMoreActionPopup(
                                currUserId = UserManager.loadCurrUserId(), targetUId = userId, userViewModel = mUserViewModel,
                                modifier = Modifier
                                    .widthIn(max = Dp(100f))
                                    .heightIn(max = Dp(400f))
                                    .background(
                                        color = androidx.compose.ui.graphics.Color.White, shape = RoundedCornerShape(Dp(6f))
                                    )
                            ) { clickType ->
                                dismiss()
                                when (clickType) {
                                    ActionClickType.SHARE -> {
                                        onShareUser(userId)
                                    }

                                    ActionClickType.BLOCK -> {
                                        onBlockUser(userId)
                                    }

                                    ActionClickType.REPORT -> {
                                        onReportUser(userId)
                                    }
                                }
                            }
                        }
                }
            })
            .show()
    }

    private fun onShareUser(userId: String) {
        // 分享
        val content = UMWeb(SUNNY_BEACH_VIEW_USER_URL_PRE + userId)
        ShareDialog.Builder(this)
            .setShareLink(content)
            .setListener(object : UmengShare.OnShareListener {
                override fun onSucceed(platform: Platform?) {
                    toast("分享成功")
                }

                override fun onError(platform: Platform?, t: Throwable) {
                    toast(t.message)
                }

                override fun onCancel(platform: Platform?) {
                    toast("分享取消")
                }
            })
            .show()
    }

    private fun onBlockUser(userId: String) {
        val currUserId = UserManager.loadCurrUserId()
        if (currUserId == userId) {
            toast("不能拉黑自己哦☺️")
            return
        }
        lifecycleScope.launch {
            val isBlockUser = mUserViewModel.isUserBlocked(uId = currUserId, targetUId = userId)
            if (isBlockUser) {
                val success = mUserViewModel.unblockUser(uId = currUserId, targetUId = userId)
                if (success) toast("已取消拉黑☺️") else toast("取消拉黑失败😭")
            } else {
                val success = mUserViewModel.blockUser(uId = currUserId, targetUId = userId)
                if (success) toast("已将该用户拉黑😤") else toast("拉黑用户失败☹️")
            }
        }
    }

    private fun onReportUser(userId: String) {
        ReportActivity.start(this, ReportType.USER, userId)
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