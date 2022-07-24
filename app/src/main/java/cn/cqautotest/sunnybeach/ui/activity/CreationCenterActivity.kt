package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.CreationCenterActivityBinding
import cn.cqautotest.sunnybeach.ktx.checkToken
import cn.cqautotest.sunnybeach.ktx.dp
import cn.cqautotest.sunnybeach.ktx.setFixOnClickListener
import cn.cqautotest.sunnybeach.ktx.takeIfLogin
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.adapter.AchievementAdapter
import cn.cqautotest.sunnybeach.ui.adapter.delegate.AdapterDelegate
import cn.cqautotest.sunnybeach.util.GridSpaceItemDecoration
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel

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
            addItemDecoration(GridSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val data = List(4) { Pair(0, 0) }
        mAchievementAdapter.setData(data)
    }

    override fun initEvent() {
        mBinding.llUserInfoContainer.setFixOnClickListener {
            takeIfLogin { userBasicInfo ->
                val userId = userBasicInfo.id
                ViewUserActivity.start(this, userId)
            }
        }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // takeIf { position == mAchievementAdapter.lastIndex }?.let { startActivity<SobIEDetailActivity>() }
        }
    }

    override fun initObserver() {
        checkToken {
            val userBasicInfo = it.getOrNull()
            mBinding.imageAvatar.loadAvatar(UserManager.currUserIsVip(), userBasicInfo?.avatar)
            mBinding.textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
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

    override fun isStatusBarDarkFont() = true
}