package cn.cqautotest.sunnybeach.ui.activity

import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.CreationCenterActivityBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.ui.adapter.AchievementAdapter
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.Glide

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/09
 * desc   : 创作中心界面
 */
class CreationCenterActivity : AppActivity() {

    private val mBinding by viewBinding(CreationCenterActivityBinding::bind)
    private val mUserViewModel by viewModels<UserViewModel>()
    private val mAchievementAdapter = AchievementAdapter()

    override fun getLayoutId(): Int = R.layout.creation_center_activity

    override fun initView() {
        mBinding.rvAchievementList.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mAchievementAdapter
            addItemDecoration(GridSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        val data = listOf(Pair(0, 0), Pair(0, 0), Pair(0, 0), Pair(0, 0))
        mAchievementAdapter.setData(data)
    }

    override fun initEvent() {
        mBinding.llUserInfoContainer.setFixOnClickListener {
            takeIfLogin { userBasicInfo ->
                val userId = userBasicInfo.id
                ViewUserActivity.start(context, userId)
            }
        }
    }

    override fun initObserver() {

    }

    override fun onResume() {
        super.onResume()
        checkToken {
            val userBasicInfo = it.getOrNull()
            val flAvatarContainer = mBinding.flAvatarContainer
            flAvatarContainer.background = UserManager.getAvatarPendant(UserManager.currUserIsVip())
            Glide.with(this)
                .load(userBasicInfo?.avatar)
                .placeholder(R.mipmap.ic_default_avatar)
                .error(R.mipmap.ic_default_avatar)
                .circleCrop()
                .into(mBinding.imageAvatar)
            mBinding.textNickName.text = userBasicInfo?.nickname ?: "账号未登录"
        }
        mUserViewModel.getAchievement().observe(this) {
            val userAchievement = it.getOrNull() ?: return@observe
            val data = arrayListOf<Pair<Int, Int>>().apply {
                add(Pair(userAchievement.atotalView, userAchievement.articleDxView))
                add(Pair(userAchievement.thumbUpTotal, userAchievement.thumbUpDx))
                add(Pair(userAchievement.fansCount, userAchievement.fansDx))
                add(Pair(userAchievement.sob, userAchievement.sobDx))
            }
            mAchievementAdapter.setData(data)
        }
    }

    override fun isStatusBarDarkFont(): Boolean = true
}