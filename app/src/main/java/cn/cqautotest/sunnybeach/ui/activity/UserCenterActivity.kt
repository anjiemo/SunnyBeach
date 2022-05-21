package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppActivity
import cn.cqautotest.sunnybeach.databinding.UserCenterActivityBinding
import cn.cqautotest.sunnybeach.ktx.*
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.PersonCenterInfo
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.ui.dialog.AddressDialog
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_VIEW_USER_URL_PRE
import cn.cqautotest.sunnybeach.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dylanc.longan.context
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import com.scwang.smart.refresh.layout.wrapper.RefreshHeaderWrapper
import timber.log.Timber
import java.io.File
import java.util.regex.Pattern

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/26
 * desc   : 个人中心界面
 */
class UserCenterActivity : AppActivity(), CameraActivity.OnCameraListener {

    private val mBinding by viewBinding(UserCenterActivityBinding::bind)
    private val mUserViewModel by viewModels<UserViewModel>()
    private var mUserBasicInfo: UserBasicInfo? = null
    private lateinit var mPersonCenterInfo: PersonCenterInfo

    /** 省 */
    private var mProvince: String? = "重庆市"

    /** 市 */
    private var mCity: String? = "重庆市"

    /** 区 */
    private var mArea: String? = "渝中区"

    override fun getLayoutId(): Int = R.layout.user_center_activity

    override fun initView() {
        val tvGetAllowance = mBinding.tvGetAllowance
        tvGetAllowance.text = getDefaultAllowanceTips()
        tvGetAllowance.setRoundRectBg(ContextCompat.getColor(this, R.color.pink), 3.dp)
        checkAllowance()
        mBinding.refreshLayout.apply {
            val headerWrapper = RefreshHeaderWrapper(View(context))
            setRefreshHeader(headerWrapper)
            setEnableLoadMore(false)
            setHeaderHeight(60f)
            // 此处 回弹动画时长 不能设置为 0，否则将无法正常回弹
            setReboundDuration(1)
            setOnRefreshListener {
                finishRefresh(0)
            }
        }
    }

    private fun checkAllowance() {
        mUserViewModel.checkAllowance().observe(this) {
            val isGetAllowance = it.getOrNull() ?: return@observe
            setupAllowanceUI(isGetAllowance)
        }
    }

    private fun getDefaultAllowanceTips() = if (UserManager.currUserIsVip()) "领取津贴" else "成为VIP"

    override fun initData() {

    }

    private fun String.manicured(): String {
        val matcher = pattern.matcher(this)
        return matcher.replaceAll("$1    $2    $3    $4    $5    $6")
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
        mBinding.tvNickName.text = mUserBasicInfo?.nickname.ifNullOrEmpty { "游客" }
        queryUserInfo()
    }

    override fun initEvent() {
        mBinding.llUserInfoContainer.setFixOnClickListener {
            takeIfLogin { userBasicInfo ->
                val userId = userBasicInfo.id
                ViewUserActivity.start(this, userId)
            }
        }
        mBinding.ivAvatar.setFixOnClickListener {
            simpleToast("此功能暂未开放")
            // CameraActivity.start(this, this)
        }
        mBinding.ivBecomeVip.setFixOnClickListener {
            startActivity<VipActivity>()
            // BrowserActivity.start(this, "https://www.sunofbeach.net/vip")
        }
        mBinding.tvGetAllowance.setFixOnClickListener {
            getAllowance()
        }
        mBinding.userCenterContent.apply {
            // region 公司、职位、技能、坐标、签名
            sbSettingCompany.setFixOnClickListener {
                // 修改公司
                ModifyUserInfoActivity.start(context, ModifyUserInfoActivity.ModifyType.COMPANY)
            }
            sbSettingJob.setFixOnClickListener {
                // 修改职位
                ModifyUserInfoActivity.start(context, ModifyUserInfoActivity.ModifyType.JOB)
            }
            sbSettingSkill.setFixOnClickListener {
                // 修改技能（擅长）
                ModifyUserInfoActivity.start(context, ModifyUserInfoActivity.ModifyType.SKILL)
            }
            sbSettingCoordinate.setFixOnClickListener {
                // 修改坐标（地区选择）
                chooseAddress()
            }
            sbSettingSign.setFixOnClickListener {
                // 修改签名
                ModifyUserInfoActivity.start(context, ModifyUserInfoActivity.ModifyType.SIGN)
            }
            // endregion

            // region 手机号、邮箱：单独校验
            // 手机
            sbSettingPhone.setFixOnClickListener {
                // TODO: 修改手机号
            }
            // 邮箱
            sbSettingEmail.setFixOnClickListener {
                // TODO: 修改邮箱
            }
            // endregion

            // region 密码（单独的修改密码接口）
            sbSettingPassword.setFixOnClickListener {
                // TODO: 修改密码
            }
            // endregion
        }
    }

    private var mAddressDialog: AddressDialog.Builder? = null

    private fun chooseAddress() {
        mAddressDialog?.dismiss()
        mAddressDialog = AddressDialog.Builder(this) //.setTitle("选择地区")
            // 设置默认省份
            .setProvince(mProvince) // 设置默认城市（必须要先设置默认省份）
            .setCity(mCity) // 不选择县级区域
            //.setIgnoreArea()
            .setListener { _, province, city, area ->
                val address: String = arrayOf(province, city, area).joinToString(separator = "/")
                val sbSettingCoordinate = mBinding.userCenterContent.sbSettingCoordinate
                if (sbSettingCoordinate.getRightText() != address) {
                    mProvince = province
                    mCity = city
                    mArea = area
                    sbSettingCoordinate.setRightText(address)
                    modifyAddress(address)
                }
            }.also {
                it.show()
            }
    }

    private fun modifyAddress(address: String) {
        takeIf { ::mPersonCenterInfo.isInitialized } ?: run {
            queryUserInfo()
            return
        }
        mUserViewModel.modifyUserInfo(mPersonCenterInfo.copy(area = address)).observe(this) { result ->
            takeIf { result.getOrNull() == true }?.let {
                simpleToast("修改成功")
                queryUserInfo()
            } ?: simpleToast("修改失败")
        }
    }

    private fun queryUserInfo() {
        val userBasicInfo = UserManager.loadUserBasicInfo()
        userBasicInfo?.let { mUserBasicInfo = it }
        mUserViewModel.queryUserInfo().observe(this) {
            mPersonCenterInfo = it.getOrNull() ?: return@observe
            val userId = mPersonCenterInfo.userId
            Timber.d("initData：===> formatted userId is $userId")
            mBinding.tvSobId.text = userId.manicured()

            val userCenterContent = mBinding.userCenterContent
            val company = mPersonCenterInfo.company.ifNullOrEmpty { "无业" }
            userCenterContent.sbSettingCompany.setRightText(company)

            val job = mPersonCenterInfo.position.ifNullOrEmpty { "游民" }

            userCenterContent.sbSettingJob.setRightText(job)
            userCenterContent.sbSettingSkill.setRightText(mPersonCenterInfo.goodAt)
            userCenterContent.sbSettingCoordinate.setRightText(mPersonCenterInfo.area)
            userCenterContent.sbSettingSign.setRightText(mPersonCenterInfo.sign)

            userCenterContent.sbSettingPhone.setRightText(mPersonCenterInfo.phoneNum)
            userCenterContent.sbSettingEmail.setRightText(mPersonCenterInfo.email)

            mBinding.ivSobQrCode.setImageBitmap(generateQRCode("${SUNNY_BEACH_VIEW_USER_URL_PRE}${mPersonCenterInfo.userId}"))
        }
    }

    private fun generateQRCode(
        content: String,
        size: Int = 400,
        bgColor: Int = Color.WHITE,
        qrColor: Int = Color.BLACK,
        margin: Int = 2
    ): Bitmap? {
        val type = HmsScan.QRCODE_SCAN_TYPE
        val options = HmsBuildBitmapOption.Creator()
            .setBitmapBackgroundColor(bgColor)
            .setBitmapColor(qrColor)
            .setBitmapMargin(margin)
            .create()
        return try {
            // 如果未设置HmsBuildBitmapOption对象，生成二维码参数options置null。
            ScanUtil.buildBitmap(content, type, size, size, options)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    private fun getAllowance() {
        mUserViewModel.getAllowance().observe(this) { result ->
            val isGetAllowance = result.getOrNull()
            if (isGetAllowance == true) simpleToast("当月津贴已领取")
            isGetAllowance ?: return@observe
            setupAllowanceUI(isGetAllowance)
        }
    }

    private fun setupAllowanceUI(isGetAllowance: Boolean) {
        val tvGetAllowance = mBinding.tvGetAllowance
        tvGetAllowance.text = if (isGetAllowance) "已领取" else getDefaultAllowanceTips()
        tvGetAllowance.isEnabled = isGetAllowance.not()
        takeIf { isGetAllowance }?.let {
            val disableTextColor = ContextCompat.getColor(this, R.color.btn_text_disable_color)
            tvGetAllowance.setTextColor(disableTextColor)
            val disableBgColor = ContextCompat.getColor(this, R.color.btn_bg_disable_color)
            tvGetAllowance.setRoundRectBg(disableBgColor, 3.dp)
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

    override fun onSelected(file: File) {
        Glide.with(this)
            .load(file)
            .placeholder(R.mipmap.ic_default_avatar)
            .error(R.mipmap.ic_default_avatar)
            .circleCrop()
            .into(mBinding.ivAvatar)
        simpleToast("暂不支持更换头像")
    }

    override fun onError(details: String) {
        toast(details)
    }

    override fun isStatusBarDarkFont(): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        mAddressDialog?.dismiss()
    }

    companion object {

        // 分隔规则
        private const val REGEX = "(\\w{4})(\\w{3})(\\w{3})(\\w{3})(\\w{3})(\\w{3})"
        private val pattern = Pattern.compile(REGEX)
    }
}