package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Dp
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.CreationCenterActivityBinding
import cn.cqautotest.sunnybeach.ktx.checkToken
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.ifLoginThen
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.adapter.AchievementAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.ui.popup.SobIntroPopup
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import cn.cqautotest.sunnybeach.widget.recyclerview.GridSpaceDecoration
import com.blankj.utilcode.util.ScreenUtils
import com.dylanc.longan.pxToDp
import com.hjq.bar.TitleBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/09
 * desc   : 创作中心界面
 */
class CreationCenterActivity : AppActivity() {

    private val mBinding by viewBinding(CreationCenterActivityBinding::bind)
    private val mUserViewModel by viewModels<UserViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mAchievementAdapter = AchievementAdapter(mAdapterDelegate)

    override fun getLayoutId(): Int = R.layout.creation_center_activity

    override fun initView() {
        mBinding.rvAchievementList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mAchievementAdapter
            addItemDecoration(GridSpaceDecoration(8.dp))
        }
    }

    override fun initData() {
        val data = List(4) { Pair(0, 0) }
        mAchievementAdapter.setData(data)
    }

    override fun initEvent() {
        mBinding.llUserInfoContainer.setFixOnClickListener {
            ifLoginThen { userBasicInfo ->
                val userId = userBasicInfo.id
                ViewUserActivity.start(this, userId)
            }
        }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // takeIf { position == mAchievementAdapter.lastIndex }?.let { startActivity<SobIEDetailActivity>() }
        }
    }

    override fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                checkToken {
                    val userBasicInfo = it.getOrNull()
                    mBinding.imageAvatar.loadAvatar(UserManager.currUserIsVip(), userBasicInfo?.avatar)
                    mBinding.textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
                }
            }
        }
        mUserViewModel.getAchievement().observe(this) {
            val userAchievement = it.getOrNull() ?: return@observe
            val data = arrayListOf<Pair<Int, Int>>().apply {
                fun add(first: Int, second: Int) {
                    add(Pair(first, second))
                }
                add(userAchievement.atotalView, userAchievement.articleDxView)
                add(userAchievement.thumbUpTotal, userAchievement.thumbUpDx)
                add(userAchievement.fansCount, userAchievement.fansDx)
                add(userAchievement.sob, userAchievement.sobDx)
            }
            mAchievementAdapter.setData(data)
        }
    }

    override fun onRightClick(titleBar: TitleBar) {
        XPopup.Builder(this)
            .isLightStatusBar(true)
            .dismissOnBackPressed(true)
            .dismissOnTouchOutside(true)
            .isDestroyOnDismiss(true)
            .asCustom(object : BottomPopupView(this), SavedStateRegistryOwner {

                private val mSavedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)

                init {
                    initLifecycle()
                }

                private fun initLifecycle() {
                    setViewTreeLifecycleOwner(this)
                    setViewTreeSavedStateRegistryOwner(this)
                    mSavedStateRegistryController.performAttach()
                    mSavedStateRegistryController.performRestore(bundleOf())
                }

                override val savedStateRegistry: SavedStateRegistry
                    get() = mSavedStateRegistryController.savedStateRegistry

                override fun getImplLayoutId(): Int = R.layout.popup_sob_intro

                override fun onCreate() {
                    super.onCreate()
                    findViewById<ComposeView>(R.id.compose_view)
                        .setContent {
                            val maxHeight = (ScreenUtils.getScreenHeight() / 4f * 3f).pxToDp()
                            SobIntroPopup(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = Dp(maxHeight.toFloat()))
                            )
                        }
                }
            })
            .show()
    }

    override fun isStatusBarDarkFont() = true
}