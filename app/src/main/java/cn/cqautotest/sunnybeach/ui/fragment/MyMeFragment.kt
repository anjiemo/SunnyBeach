package cn.cqautotest.sunnybeach.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils
import cn.cqautotest.sunnybeach.ktx.context
import cn.cqautotest.sunnybeach.ktx.createDefaultStyleBadge
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.startActivity
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.BrowserActivity
import cn.cqautotest.sunnybeach.ui.activity.CollectionListActivity
import cn.cqautotest.sunnybeach.ui.activity.CreationCenterActivity
import cn.cqautotest.sunnybeach.ui.activity.HotArticleListActivity
import cn.cqautotest.sunnybeach.ui.activity.LoginActivity
import cn.cqautotest.sunnybeach.ui.activity.MessageCenterActivity
import cn.cqautotest.sunnybeach.ui.activity.MineArticleListActivity
import cn.cqautotest.sunnybeach.ui.activity.RichListActivity
import cn.cqautotest.sunnybeach.ui.activity.SettingActivity
import cn.cqautotest.sunnybeach.ui.activity.UserCenterActivity
import cn.cqautotest.sunnybeach.ui.activity.VipActivity
import cn.cqautotest.sunnybeach.ui.activity.WallpaperActivity
import cn.cqautotest.sunnybeach.ui.activity.weather.MainActivity
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import cn.cqautotest.sunnybeach.util.MAKE_COMPLAINTS_URL
import cn.cqautotest.sunnybeach.util.UmengReportKey
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import com.blankj.utilcode.util.ClipboardUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dylanc.longan.viewLifecycleScope
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 个人中心界面
 */
@ExperimentalBadgeUtils
class MyMeFragment : TitleBarFragment<AppActivity>() {

    private val mBinding: MyMeFragmentBinding by viewBinding()
    private val mMsgViewModel by viewModels<MsgViewModel>()
    private val badgeDrawable by lazy {
        createDefaultStyleBadge(requireContext(), 0).apply {
            val meContent = mBinding.meContent
            BadgeUtils.attachBadgeDrawable(this, meContent.ivMsgCenter)
        }
    }
    private var mCallback: Runnable? = null
    private val mLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        takeIf { activityResult.resultCode == Activity.RESULT_OK }?.let { mCallback?.run() }
    }

    override fun getLayoutId(): Int = R.layout.my_me_fragment

    override fun initView() {
        mBinding.meContent.apply {
            Glide.with(context)
                .load(R.mipmap.ic_vip_banner_bg)
                .transform(RoundedCorners(10.dp))
                .into(ivVipBanner)
        }
    }

    override fun initData() {
        viewLifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                updateUserInfoUI()
                mMsgViewModel.getUnReadMsgCount().collectLatest {
                    badgeDrawable.isVisible = it.hasUnReadMsg
                }
            }
        }
        LiveBusUtils.busReceive<Unit>(viewLifecycleOwner, LiveBusKeyConfig.BUS_LOGIN_INFO_UPDATE) {
            viewLifecycleScope.launch {
                updateUserInfoUI()
                mMsgViewModel.getUnReadMsgCount().collectLatest {
                    badgeDrawable.isVisible = it.hasUnReadMsg
                }
            }
        }
    }

    /**
     * 更新用户信息
     */
    private fun updateUserInfoUI() {
        val userBasicInfo = UserManager.loadUserBasicInfo()
        Timber.d("initData：===> userBasicInfo is $userBasicInfo")
        val isVip = userBasicInfo?.isVip.equals("1")
        mBinding.meContent.apply {
            imageAvatar.loadAvatar(isVip, userBasicInfo?.avatar)
            textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
            textNickName.isSelected = isVip
        }
    }

    override fun initEvent() {
        with(mBinding.meContent) {
            // 跳转到用户中心
            llUserInfoContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.USER_CENTER)
                when (UserManager.loadUserBasicInfo()) {
                    null -> {
                        mLoginLauncher.launch(LoginActivity.createIntent(requireContext()))
                        toast(R.string.please_login_first)
                    }
                    else -> context.startActivity<UserCenterActivity>()
                }
            }
            // 会员详情
            flVipBannerContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.JOIN_VIP)
                context.startActivity<VipActivity>()
            }

            // 跳转到消息中心
            messageCenterContainer.setFixOnClickListener {
                viewLifecycleScope.launchWhenCreated {
                    afterWaitingForLogin { context.startActivity<MessageCenterActivity>() }
                }
            }
            // 跳转到富豪榜列表
            richListContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.RICH_LIST)
                context.startActivity<RichListActivity>()
            }
            // 跳转到天气预报
            weatherContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.WEATHER_FORECAST)
                context.startActivity<MainActivity>()
            }
            // 小默文章列表
            hotArticleListContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.ANJIEMO_ARTICLE)
                context.startActivity<HotArticleListActivity>()
            }
            // 跳转到用户内容管理界面
            userArticleListContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.MINE_ARTICLE)
                viewLifecycleScope.launchWhenCreated {
                    afterWaitingForLogin { context.startActivity<MineArticleListActivity>() }
                }
            }
            // 跳转到创作中心
            creationCenterContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.CREATION_CENTER)
                viewLifecycleScope.launchWhenCreated {
                    afterWaitingForLogin { context.startActivity<CreationCenterActivity>() }
                }
            }
            // 我的收藏
            collectionContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.COLLECTIONS)
                viewLifecycleScope.launchWhenCreated {
                    afterWaitingForLogin { context.startActivity<CollectionListActivity>() }
                }
            }
            // 跳转到高清壁纸
            wallpaperContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.VIEW_HD_WALLPAPER)
                context.startActivity<WallpaperActivity>()
            }
            // 加入QQ群聊
            joinQQGroupContainer.setFixOnClickListener {
                MessageDialog.Builder(requireContext())
                    .setTitle("系统消息")
                    .setMessage("暗号：阳光沙滩APP来的")
                    .setConfirm("我要加群")
                    .setCancel("点错了")
                    .setCanceledOnTouchOutside(false)
                    .setListener {
                        MobclickAgent.onEvent(context, UmengReportKey.JOIN_QQ_GROUP)
                        ClipboardUtils.copyText("阳光沙滩APP来的")
                        when (joinQQGroup()) {
                            true -> toast("正在唤起手Q...")
                            else -> toast("未安装手Q或安装的版本不支持")
                        }
                    }.show()
            }
            // 跳转到意见反馈
            feedbackContainer.setFixOnClickListener {
                // check userBasicInfo is null, anonymous feedback if empty.
                when (val userInfo = UserManager.loadUserBasicInfo()) {
                    null -> BrowserActivity.start(context, MAKE_COMPLAINTS_URL)
                    else -> BrowserActivity.start(context, MAKE_COMPLAINTS_URL, true, userInfo.id, userInfo.nickname, userInfo.avatar)
                }
            }
            // 跳转到设置
            settingContainer.setFixOnClickListener { context.startActivity<SettingActivity>() }
        }
    }

    /**
     * 等待登录后执行给定的 Lambda
     */
    private suspend fun afterWaitingForLogin(block: () -> Unit) = suspendCoroutine { continuation ->
        when (UserManager.isLogin()) {
            true -> {
                block.invoke()
                continuation.resume(Unit)
            }
            else -> loginOrElse {
                block.invoke()
                continuation.resume(Unit)
            }
        }
    }

    /**
     * 去登录或者执行 Runnable
     */
    private fun loginOrElse(callback: Runnable? = null) {
        mCallback = callback
        when (UserManager.loadUserBasicInfo()) {
            null -> {
                mLoginLauncher.launch(LoginActivity.createIntent(requireContext()))
                toast(R.string.please_login_first)
            }
            else -> callback?.run()
        }
    }

    /**
     *
     * QQ群加群组件：https://qun.qq.com/join.html
     *
     * 发起添加群流程。群号：阳光沙滩大中华区技术交流群(582845417) 的 key 为：_ZSAyNbMN2gme8AxZuUoUhIGeFiD6XaU
     * 调用 joinQQGroup() 即可发起手Q客户端申请加群 阳光沙滩大中华区技术交流群(582845417)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     */
    private fun joinQQGroup(key: String = "_ZSAyNbMN2gme8AxZuUoUhIGeFiD6XaU"): Boolean {
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            false
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