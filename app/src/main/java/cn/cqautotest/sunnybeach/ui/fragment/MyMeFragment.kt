package cn.cqautotest.sunnybeach.ui.fragment

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.app.TitleBarFragment
import cn.cqautotest.sunnybeach.databinding.MyMeFragmentBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.activity.*
import cn.cqautotest.sunnybeach.ui.activity.weather.MainActivity
import cn.cqautotest.sunnybeach.ui.dialog.MessageDialog
import cn.cqautotest.sunnybeach.util.MAKE_COMPLAINTS_URL
import cn.cqautotest.sunnybeach.util.UmengReportKey
import cn.cqautotest.sunnybeach.viewmodel.MsgViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dylanc.longan.viewLifecycleScope
import com.google.android.material.badge.BadgeUtils
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 个人中心界面
 */
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
                mMsgViewModel.getUnReadMsgCount().collectLatest {
                    badgeDrawable.isVisible = it.hasUnReadMsg
                }
            }
        }
        viewLifecycleScope.launch {
            checkToken {
                val userBasicInfo = it.getOrNull()
                Timber.d("initData：===> userBasicInfo is $userBasicInfo")
                val isVip = userBasicInfo?.isVip.equals("1")
                mBinding.meContent.apply {
                    imageAvatar.loadAvatar(isVip, userBasicInfo?.avatar)
                    textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
                    textNickName.isSelected = isVip
                }
            }
        }
    }

    override fun initEvent() {
        with(mBinding.meContent) {
            // 跳转到用户中心
            llUserInfoContainer.setFixOnClickListener {
                ifLoginThen {
                    MobclickAgent.onEvent(context, UmengReportKey.USER_CENTER)
                    context.startActivity<UserCenterActivity>()
                }
            }
            // 会员详情
            tvMembershipDetail.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.JOIN_VIP)
                context.startActivity<VipActivity>()
            }

            // 跳转到消息中心
            messageCenterContainer.setFixOnClickListener { ifLoginThen { requireContext().startActivity<MessageCenterActivity>() } }
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
                ifLoginThen {
                    MobclickAgent.onEvent(context, UmengReportKey.MINE_ARTICLE)
                    context.startActivity<MineArticleListActivity>()
                }
            }
            // 跳转到创作中心
            creationCenterContainer.setFixOnClickListener {
                ifLoginThen {
                    MobclickAgent.onEvent(context, UmengReportKey.CREATION_CENTER)
                    context.startActivity<CreationCenterActivity>()
                }
            }
            // 我的收藏
            collectionContainer.setFixOnClickListener {
                ifLoginThen {
                    MobclickAgent.onEvent(context, UmengReportKey.COLLECTIONS)
                    context.startActivity<CollectionListActivity>()
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
                    .setListener {
                        MobclickAgent.onEvent(context, UmengReportKey.JOIN_QQ_GROUP)
                        when (joinQQGroup()) {
                            true -> toast("正在唤起手Q...")
                            else -> toast("未安装手Q或安装的版本不支持")
                        }
                    }.show()
            }
            // 跳转到意见反馈
            feedbackContainer.setFixOnClickListener {
                viewLifecycleScope.launchWhenCreated {
                    checkToken {
                        // check userBasicInfo is null, anonymous feedback if empty.
                        val userBasicInfo = UserManager.loadUserBasicInfo() ?: run {
                            BrowserActivity.start(context, MAKE_COMPLAINTS_URL)
                            return@checkToken
                        }
                        val (avatar, _, _, id, _, _, nickname, _, _) = userBasicInfo
                        BrowserActivity.start(context, MAKE_COMPLAINTS_URL, true, id, nickname, avatar)
                    }
                }
            }
            // 跳转到设置
            settingContainer.setFixOnClickListener { context.startActivity<SettingActivity>() }
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