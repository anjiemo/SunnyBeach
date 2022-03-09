package cn.cqautotest.sunnybeach.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
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
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.scwang.smart.refresh.layout.wrapper.RefreshHeaderWrapper
import timber.log.Timber
import java.io.File

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
            setOnRefreshListener {
                finishRefresh(0)
            }
        }
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
        mUserViewModel.queryUserInfo().observe(this) {
            val personCenterInfo = it.getOrNull() ?: return@observe
            val userCenterContent = mBinding.userCenterContent
            userCenterContent.sbSettingCompany.rightText = personCenterInfo.company
            userCenterContent.sbSettingJob.rightText = personCenterInfo.position
            userCenterContent.sbSettingSkill.rightText = personCenterInfo.goodAt
            userCenterContent.sbSettingCoordinate.rightText = personCenterInfo.area
            userCenterContent.sbSettingSign.rightText = personCenterInfo.sign

            userCenterContent.sbSettingPhone.rightText = personCenterInfo.phoneNum
            userCenterContent.sbSettingEmail.rightText = personCenterInfo.email

            mBinding.ivSobQrCode.setImageBitmap(generateQRCode("sob://user/${personCenterInfo.userId}"))
        }
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
            startActivity<VipActivity>()
            // BrowserActivity.start(this, "https://www.sunofbeach.net/vip")
        }
        mBinding.tvGetAllowance.setFixOnClickListener {
            getAllowance()
        }
        mBinding.clScanQrCode.setFixOnClickListener {
            toast("hhh")
            // “QRCODE_SCAN_TYPE”和“DATAMATRIX_SCAN_TYPE”表示只扫描QR和Data Matrix的码
            val options = HmsScanAnalyzerOptions.Creator()
                .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE)
                .create()
            ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options)
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
            result.getOrNull()?.let {
                val tvGetAllowance = mBinding.tvGetAllowance
                tvGetAllowance.text = "已领取"
                tvGetAllowance.isEnabled = it.not()
                val disableTextColor = ContextCompat.getColor(this, R.color.btn_text_disable_color)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            // 导入图片扫描返回结果
            val obj = data.getParcelableExtra(ScanUtil.RESULT) as HmsScan?
            if (obj != null) {
                // 展示解码结果
                showResult(obj)
            }
        }
    }

    private fun showResult(result: HmsScan) {
        val linkUrl = result.getLinkUrl()
        val theme = linkUrl.theme
        val linkValue = linkUrl.linkValue
        Timber.d("showResult：===> theme is $theme linkValue is $linkValue")
    }

    companion object {
        private const val REQUEST_CODE_SCAN_ONE = 0x0000
    }
}