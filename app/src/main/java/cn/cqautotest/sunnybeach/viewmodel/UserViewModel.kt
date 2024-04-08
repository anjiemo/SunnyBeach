package cn.cqautotest.sunnybeach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.cqautotest.sunnybeach.http.network.Repository
import cn.cqautotest.sunnybeach.manager.UserManager
import cn.cqautotest.sunnybeach.model.ModifyPwd
import cn.cqautotest.sunnybeach.model.PersonCenterInfo
import cn.cqautotest.sunnybeach.model.ReportType
import cn.cqautotest.sunnybeach.model.SmsInfo
import cn.cqautotest.sunnybeach.model.User
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.paging.source.RichPagingSource
import cn.cqautotest.sunnybeach.util.I_LOVE_ANDROID_SITE_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_SITE_BASE_URL
import cn.cqautotest.sunnybeach.util.StringUtil
import com.blankj.utilcode.util.RegexUtils
import timber.log.Timber
import java.io.File

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 用户 ViewModel
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val phoneLiveData = MutableLiveData<String>()

    val userAvatarLiveData = phoneLiveData.switchMap { account ->
        Repository.queryUserAvatar(account)
    }

    /**
     * 富豪榜
     */
    val richListFlow = Pager(config = PagingConfig(30),
        pagingSourceFactory = {
            RichPagingSource()
        }).flow.cachedIn(viewModelScope)

    /**
     * We only support http and https protocols.
     */
    fun checkScheme(scheme: String) = scheme == "http" || scheme == "https"

    fun checkAuthority(authority: String): Boolean {
        val sobSiteTopDomain = StringUtil.getTopDomain(SUNNY_BEACH_SITE_BASE_URL)
        val loveSiteTopDomain = StringUtil.getTopDomain(I_LOVE_ANDROID_SITE_BASE_URL)

        Timber.d("checkAuthority：===> authority is $authority")
        Timber.d("checkAuthority：===> sobSiteTopDomain is $sobSiteTopDomain")
        Timber.d("checkAuthority：===> loveSiteTopDomain is $loveSiteTopDomain")

        fun String.delete3W() = replace("www.", "")
        val sobAuthority = authority.delete3W() == sobSiteTopDomain
        val loveAuthority = authority.delete3W() == loveSiteTopDomain
        return sobAuthority || loveAuthority
    }

    /**
     * Sob site userId is long type, we need check.
     */
    fun checkUserId(userId: String) = userId.isNotBlank() && userId.toLongOrNull() != null

    /**
     * 举报
     */
    fun report(reportType: ReportType, contentId: String, url: String, why: String) =
        Repository.report(reportType = reportType, contentId = contentId, url = url, why = why)

    /**
     * 修改用户头像
     */
    fun modifyAvatar(avatarUrl: String) = Repository.modifyAvatar(avatarUrl)

    /**
     * 根据分类 id 上传用户中心图片
     */
    suspend fun uploadUserCenterImageByCategoryId(imageFile: File, categoryId: String) =
        Repository.uploadUserCenterImageByCategoryId(imageFile, categoryId)

    /**
     * 发送邮件
     */
    fun sendEmail(email: String) = Repository.sendEmail(email)

    /**
     * 找回密码（通过短信找回）
     */
    fun modifyPasswordBySms(smsCode: String, user: User) = Repository.modifyPasswordBySms(smsCode, user)

    /**
     * 修改密码（通过旧密码修改）
     */
    fun modifyPasswordByOldPwd(modifyPwd: ModifyPwd) = Repository.modifyPasswordByOldPwd(modifyPwd)

    /**
     * 检查手机验证码是否正确
     */
    fun checkSmsCode(phoneNumber: String, smsCode: String) = Repository.checkSmsCode(phoneNumber, smsCode)

    /**
     * 获取找回密码的手机验证码（找回密码）
     */
    fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = Repository.sendForgetSmsVerifyCode(smsInfo)

    /**
     * 注册账号
     */
    fun registerAccount(smsCode: String, user: User) = Repository.registerAccount(smsCode, user)

    /**
     * 获取注册的手机验证码（注册）
     */
    fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = Repository.sendRegisterSmsVerifyCode(smsInfo)

    /**
     * 获取当前用户 Sob 币的收支（Income & Expenditures）明细列表
     */
    fun getMeSobIEDetailList(page: Int) = Repository.getSobIEDetailList(UserManager.loadCurrUserId(), page)

    /**
     * 获取用户 Sob 币的收支（Income & Expenditures）明细列表
     */
    fun getSobIEDetailList(userId: String, page: Int) = Repository.getSobIEDetailList(userId, page)

    /**
     * 获取VIP列表
     */
    fun getVipUserList() = Repository.getVipUserList()

    /**
     * 个人中心获取成就
     */
    fun getAchievement() = Repository.getAchievement()

    /**
     * 个人中心获取自己的sob币总数
     */
    fun queryTotalSobCount() = Repository.queryTotalSobCount()

    /**
     * 个人中心修改账号信息
     */
    fun modifyUserInfo(personCenterInfo: PersonCenterInfo) = Repository.modifyUserInfo(personCenterInfo)

    /**
     * 个人中心获取账号信息
     */
    fun queryUserInfo() = Repository.queryUserInfo()

    /**
     * 检查是否领取过津贴
     */
    fun checkAllowance() = Repository.checkAllowance()

    /**
     * 领取津贴
     */
    fun getAllowance() = Repository.getAllowance()

    /**
     * 取消关注用户
     */
    fun unfollowUser(userId: String) = Repository.unfollowUser(userId)

    /**
     * 关注用户
     */
    fun followUser(userId: String) = Repository.followUser(userId)

    /**
     * 自己与目标用户的关注状态
     *
     * 0：表示没有关注对方，可以显示为：关注
     * 1：表示对方关注自己，可以显示为：回粉
     * 2：表示已经关注对方，可以显示为：已关注
     * 3：表示相互关注，可以显示为：相互关注
     */
    fun followState(userId: String) = Repository.followState(userId)

    /**
     * 获取用户信息
     */
    fun getUserInfo(userId: String) = Repository.getUserInfo(userId)

    /**
     * 获取当前用户的成就
     */
    fun getAchievement(userId: String = "") = Repository.getAchievement(userId)

    /**
     * 通过账号（手机号）获取用户头像
     */
    fun queryUserAvatar(account: String) {
        phoneLiveData.value = account
    }

    /**
     * 退出登录
     */
    fun logout() = Repository.logout()

    /**
     * 用户账号登录
     */
    fun login(userAccount: String, password: String, captcha: String) = when {
        userAccount.isUserAccountValid().not() -> checkErrorResult("手机号码格式错误")
        password.isPasswordValid().not() -> checkErrorResult("密码长度不能低于5位")
        captcha.isVerifyCodeValid().not() -> checkErrorResult("验证码不能为空")
        else -> Repository.login(userAccount, password, captcha)
    }

    /**
     * 检查错误结果
     */
    private fun checkErrorResult(msg: String) = MutableLiveData<Result<UserBasicInfo>>(Result.failure(IllegalArgumentException(msg)))

    /**
     * 手机号码格式检查
     */
    private fun String.isUserAccountValid(): Boolean = RegexUtils.isMobileExact(this)

    /**
     * 账号密码长度检查
     */
    private fun String.isPasswordValid(): Boolean = length > 5

    /**
     * 验证码检查
     */
    private fun String.isVerifyCodeValid(): Boolean = isNotEmpty()
}