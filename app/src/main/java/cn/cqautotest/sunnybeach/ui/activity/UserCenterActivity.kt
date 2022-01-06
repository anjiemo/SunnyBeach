package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.UserCenterActivityBinding
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.util.*
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/26
 * desc   : 个人中心界面
 */
class UserCenterActivity : AppActivity(), CameraActivity.OnCameraListener {

    private val mBinding by viewBinding<UserCenterActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()
    private var mUserBasicInfo: UserBasicInfo? = null

    override fun getLayoutId(): Int = R.layout.user_center_activity

    override fun initView() {
        val tvGetAllowance = mBinding.tvGetAllowance
        tvGetAllowance.text = getDefaultAllowanceTips()
        tvGetAllowance.setRoundRectBg(ContextCompat.getColor(this, R.color.pink), 3.dp)
        checkAllowance()
    }

    private fun checkAllowance(block: (isGetAllowance: Boolean) -> Unit = {}) {
        val tvGetAllowance = mBinding.tvGetAllowance
        mUserViewModel.checkAllowance().observe(this) {
            val isGetAllowance = it.getOrNull() ?: return@observe
            tvGetAllowance.text = if (isGetAllowance) "已领取" else getDefaultAllowanceTips()
            tvGetAllowance.isEnabled = isGetAllowance.not()
            takeIf { isGetAllowance }?.let {
                val disableTextColor = ContextCompat.getColor(this, R.color.btn_text_disable_color)
                tvGetAllowance.setTextColor(disableTextColor)
                val disableBgColor = ContextCompat.getColor(this, R.color.btn_bg_disable_color)
                tvGetAllowance.setRoundRectBg(disableBgColor, 3.dp)
            }
            block.invoke(isGetAllowance)
        }
    }

    private fun getDefaultAllowanceTips() = if (UserManager.currUserIsVip()) "领取津贴" else "成为VIP"

    override fun initData() {
        val userBasicInfo = UserManager.loadUserBasicInfo()
        userBasicInfo?.let { mUserBasicInfo = it }
    }

    override fun onResume() {
        super.onResume()
        Glide.with(this)
            .load(mUserBasicInfo?.avatar)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(mBinding.ivAvatar)
        mBinding.tvNickName.text = mUserBasicInfo?.nickname ?: "游客"
    }

    override fun initEvent() {
        val tvGetAllowance = mBinding.tvGetAllowance
        mBinding.llUserInfoContainer.setFixOnClickListener {
            takeIfLogin { userBasicInfo ->
                val userId = userBasicInfo.id
                ViewUserActivity.start(context, userId)
            }
        }
        mBinding.ivAvatar.setFixOnClickListener {
            simpleToast("此功能暂未开放")
            // CameraActivity.start(this, this)
        }
        mBinding.ivBecomeVip.setFixOnClickListener {
            BrowserActivity.start(this, "https://www.sunofbeach.net/vip")
        }
        tvGetAllowance.setFixOnClickListener {
            mUserViewModel.getAllowance().observe(this) { result ->
                result.getOrNull()?.let {
                    tvGetAllowance.text = "已领取"
                    tvGetAllowance.isEnabled = it.not()
                    val disableTextColor =
                        ContextCompat.getColor(this, R.color.btn_text_disable_color)
                    tvGetAllowance.setTextColor(disableTextColor)
                    val disableBgColor = ContextCompat.getColor(this, R.color.btn_bg_disable_color)
                    tvGetAllowance.setRoundRectBg(disableBgColor, 3.dp)
                    checkAllowance { isGetAllowance ->
                        takeIf { isGetAllowance }?.let {
                            simpleToast("当月津贴已领取")
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun initObserver() {
        mUserViewModel.getAchievement().observe(this) {
            val userAchievement = it.getOrNull() ?: return@observe
            mBinding.tvNickName.text = mUserBasicInfo?.nickname ?: "游客"
            // 此字段在 v2 版本的接口将会修改
            mBinding.tvVip.text = when (mUserBasicInfo?.isVip) {
                "1" -> "正式会员"
                "0" -> "普通会员"
                else -> "普通会员"
            }
            mBinding.tvSobCurrency.text = "SOB币：${userAchievement.sob}"
            mBinding.tvDynamicNum.text = userAchievement.momentCount.toString()
            mBinding.tvFollowNum.text = userAchievement.followCount.toString()
            mBinding.tvFansNum.text = userAchievement.fansCount.toString()
        }
    }

    override fun onSelected(file: File?) {
        file ?: return
        Glide.with(this)
            .load(file)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(mBinding.ivAvatar)
        simpleToast("暂不支持更换头像")
    }
}