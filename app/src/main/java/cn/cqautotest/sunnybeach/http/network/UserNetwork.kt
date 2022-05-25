package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.model.*
import okhttp3.MultipartBody

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 用户信息获取
 */
object UserNetwork : INetworkApi {

    suspend fun report(report: Report) = userApi.report(report)

    suspend fun modifyAvatar(avatarUrl: String) = userApi.modifyAvatar(avatarUrl)

    suspend fun uploadUserCenterImageByCategoryId(part: MultipartBody.Part, categoryId: String) =
        userApi.uploadUserCenterImageByCategoryId(part, categoryId)

    suspend fun sendEmail(email: String) = userApi.sendEmail(email)

    suspend fun modifyPasswordBySms(smsCode: String, user: User) = userApi.modifyPasswordBySms(smsCode, user)

    suspend fun modifyPasswordByOldPwd(modifyPwd: ModifyPwd) = userApi.modifyPasswordByOldPwd(modifyPwd)

    suspend fun checkSmsCode(phoneNumber: String, smsCode: String) = userApi.checkSmsCode(phoneNumber, smsCode)

    suspend fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = userApi.sendForgetSmsVerifyCode(smsInfo)

    suspend fun registerAccount(smsCode: String, user: User) = userApi.registerAccount(smsCode, user)

    suspend fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = userApi.sendRegisterSmsVerifyCode(smsInfo)

    suspend fun getSobIEDetailList(userId: String, page: Int) = userApi.getSobIEDetailList(userId, page)

    suspend fun getVipUserList() = userApi.getVipUserList()

    suspend fun getAchievement() = userApi.getAchievement()

    suspend fun queryTotalSobCount() = userApi.queryTotalSobCount()

    suspend fun modifyUserInfo(personCenterInfo: PersonCenterInfo) = userApi.modifyUserInfo(personCenterInfo)

    suspend fun queryUserInfo() = userApi.queryUserInfo()

    suspend fun getRichList(count: Int = 100) = userApi.getRichList(count)

    suspend fun queryUserAvatar(account: String) = userApi.queryUserAvatar(account)

    suspend fun logout() = userApi.logout()

    suspend fun checkToken() = userApi.checkToken()

    suspend fun login(captcha: String, user: User) = userApi.login(captcha, user)

    suspend fun getAchievement(userId: String) = userApi.getAchievement(userId)

    suspend fun getUserInfo(userId: String) = userApi.getUserInfo(userId)

    suspend fun followState(userId: String) = userApi.followState(userId)

    suspend fun followUser(userId: String) = userApi.followUser(userId)

    suspend fun unfollowUser(userId: String) = userApi.unfollowUser(userId)

    suspend fun checkAllowance() = userApi.checkAllowance()

    suspend fun getAllowance() = userApi.getAllowance()
}