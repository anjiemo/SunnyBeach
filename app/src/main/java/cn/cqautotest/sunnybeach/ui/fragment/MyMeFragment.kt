package cn.cqautotest.sunnybeach.ui.fragment

import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.*
import cn.cqautotest.sunnybeach.ui.activity.weather.MainActivity
import cn.cqautotest.sunnybeach.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 个人中心界面
 */
class MyMeFragment : TitleBarFragment<AppActivity>() {

    private val mBinding: MyMeFragmentBinding by viewBinding()

    override fun getLayoutId(): Int = R.layout.my_me_fragment

    override fun onFragmentResume(first: Boolean) {
        super.onFragmentResume(first)
        checkToken {
            val userBasicInfo = it.getOrNull()
            val meContent = mBinding.meContent
            val flAvatarContainer = meContent.flAvatarContainer
            flAvatarContainer.background = UserManager.getAvatarPendant(UserManager.currUserIsVip())
            Glide.with(this)
                .load(userBasicInfo?.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(meContent.imageAvatar)
            meContent.textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
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
            requireContext().startActivity<CreationCenterActivity>()
        }
        meContent.weatherContainer.setFixOnClickListener {
            requireContext().startActivity<MainActivity>()
        }
        meContent.feedbackContainer.setFixOnClickListener {
            checkToken {
                val userBasicInfo = UserManager.loadUserBasicInfo() ?: run {
                    BrowserActivity.start(requireContext(), MAKE_COMPLAINTS_URL)
                    return@checkToken
                }
                val (avatar, _, _, id, _, _, nickname, _, _) = userBasicInfo
                BrowserActivity.start(requireContext(), MAKE_COMPLAINTS_URL, id, nickname, avatar)
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