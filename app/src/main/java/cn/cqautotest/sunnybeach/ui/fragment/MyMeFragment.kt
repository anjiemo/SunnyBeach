package cn.cqautotest.sunnybeach.ui.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.*
import cn.cqautotest.sunnybeach.ui.activity.weather.MainActivity
import cn.cqautotest.sunnybeach.util.MAKE_COMPLAINTS_URL
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import com.dylanc.longan.viewLifecycleScope
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

    override fun initView() {}

    override fun initData() {}

    override fun onResume() {
        super.onResume()
        with(mBinding.meContent) {
            viewLifecycleScope.launchWhenCreated {
                checkToken {
                    val userBasicInfo = it.getOrNull()
                    imageAvatar.loadAvatar(UserManager.currUserIsVip(), userBasicInfo?.avatar)
                    textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
                }
            }
        }
        mMsgViewModel.getUnReadMsgCount().observe(viewLifecycleOwner) {
            val unReadMsgCount = it.getOrNull() ?: return@observe
            badgeDrawable.isVisible = unReadMsgCount.hasUnReadMsg
        }
    }

    override fun initEvent() {
        with(mBinding.meContent) {
            // 跳转到用户中心
            llUserInfoContainer.setFixOnClickListener { ifLoginThen { requireContext().startActivity<UserCenterActivity>() } }
            // 小默文章列表
            hotArticleListContainer.setFixOnClickListener { requireContext().startActivity<HotArticleListActivity>() }
            // 跳转到富豪榜列表
            richListContainer.setFixOnClickListener { requireContext().startActivity<RichListActivity>() }
            // 跳转到消息中心
            messageCenterContainer.setFixOnClickListener { ifLoginThen { requireContext().startActivity<MessageCenterActivity>() } }
            // 跳转到创作中心
            creationCenterContainer.setFixOnClickListener { ifLoginThen { requireContext().startActivity<CreationCenterActivity>() } }
            // 我的收藏
            collectionContainer.setFixOnClickListener { ifLoginThen { requireContext().startActivity<CollectionListActivity>() } }
            // 跳转到高清壁纸
            wallpaperContainer.setFixOnClickListener { requireContext().startActivity<WallpaperActivity>() }
            // 跳转到天气预报
            weatherContainer.setFixOnClickListener { requireContext().startActivity<MainActivity>() }
            // 跳转到意见反馈
            feedbackContainer.setFixOnClickListener {
                viewLifecycleScope.launchWhenCreated {
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
            }
            // 跳转到设置
            settingContainer.setFixOnClickListener { requireContext().startActivity<SettingActivity>() }
        }
    }

    override fun isStatusBarDarkFont() = false

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyMeFragment()
    }
}