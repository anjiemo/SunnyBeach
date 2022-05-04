package cn.cqautotest.sunnybeach.ui.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.*
import cn.cqautotest.sunnybeach.ui.activity.weather.MainActivity
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import com.google.android.material.badge.BadgeUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 个人中心界面
 */
@SuppressLint("UnsafeOptInUsageError")
class MyMeFragment : TitleBarFragment<AppActivity>() {

    private val mBinding: MyMeFragmentBinding by viewBinding()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val badgeDrawable by lazy {
        createDefaultStyleBadge(requireContext(), 0).apply {
            val meContent = mBinding.meContent
            BadgeUtils.attachBadgeDrawable(this, meContent.ivMsgCenter)
        }
    }

    override fun getLayoutId(): Int = R.layout.my_me_fragment

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        val meContent = mBinding.meContent
        checkToken {
            val userBasicInfo = it.getOrNull()
            meContent.imageAvatar.loadAvatar(UserManager.currUserIsVip(), userBasicInfo?.avatar)
            meContent.textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
        }
        mMsgViewModel.getUnReadMsgCount().observe(viewLifecycleOwner) {
            val unReadMsgCount = it.getOrNull() ?: return@observe
            badgeDrawable.isVisible = unReadMsgCount.hasUnReadMsg
        }
    }

    override fun initEvent() {
        val meContent = mBinding.meContent
        meContent.llUserInfoContainer.setFixOnClickListener {
            takeIfLogin {
                requireContext().startActivity<UserCenterActivity>()
            }
        }
        meContent.richListContainer.setFixOnClickListener {
            requireContext().startActivity<RichListActivity>()
        }
        meContent.messageCenterContainer.setFixOnClickListener {
            takeIfLogin {
                requireContext().startActivity<MessageCenterActivity>()
            }
        }
        meContent.creationCenterContainer.setFixOnClickListener {
            takeIfLogin {
                requireContext().startActivity<CreationCenterActivity>()
            }
        }
        meContent.wallpaperContainer.setFixOnClickListener {
            requireContext().startActivity<WallpaperActivity>()
        }
        meContent.weatherContainer.setFixOnClickListener {
            requireContext().startActivity<MainActivity>()
        }
        meContent.feedbackContainer.setFixOnClickListener {
            checkToken {
                // check userBasicInfo is null, anonymous feedback if empty.
                val userBasicInfo = UserManager.loadUserBasicInfo() ?: run {
                    BrowserActivity.start(requireContext(), MAKE_COMPLAINTS_URL)
                    return@checkToken
                }
                val (avatar, _, _, id, _, _, nickname, _, _) = userBasicInfo
                BrowserActivity.start(requireContext(), MAKE_COMPLAINTS_URL, true, id, nickname, avatar)
            }
        }
        meContent.settingContainer.setFixOnClickListener {
            requireContext().startActivity<SettingActivity>()
        }
    }

    override fun initData() {}

    override fun initView() {}

    override fun isStatusBarDarkFont(): Boolean = false

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyMeFragment()
    }
}