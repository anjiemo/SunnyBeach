package cn.cqautotest.sunnybeach.viewmodel.app

import cn.cqautotest.sunnybeach.http.ServiceCreator
import cn.cqautotest.sunnybeach.http.request.api.QaApi
import cn.cqautotest.sunnybeach.http.request.api.UserApi
import cn.cqautotest.sunnybeach.model.ModifyPwd
import cn.cqautotest.sunnybeach.model.SmsInfo
import cn.cqautotest.sunnybeach.model.User

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 用户信息获取
 */
object UserNetwork {

    private val userApi = ServiceCreator.create<UserApi>()
    private val qaApi = ServiceCreator.create<QaApi>()

    suspend fun modifyPassword(modifyPwd: ModifyPwd) = userApi.modifyPassword(modifyPwd)

    suspend fun checkSmsCode(phoneNumber: String, smsCode: String) =
        userApi.checkSmsCode(phoneNumber, smsCode)

    suspend fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = userApi.sendForgetSmsVerifyCode(smsInfo)

    suspend fun registerAccount(smsCode: String, user: User) =
        userApi.registerAccount(smsCode, user)

    suspend fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) =
        userApi.sendRegisterSmsVerifyCode(smsInfo)

    suspend fun getSobIEDetailList(userId: String, page: Int) =
        userApi.getSobIEDetailList(userId, page)

    suspend fun getVipUserList() = userApi.getVipUserList()

    suspend fun getAchievement() = userApi.getAchievement()

    suspend fun queryTotalSobCount() = userApi.queryTotalSobCount()

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

    suspend fun getUserQaList(userId: String, page: Int) = qaApi.getUserQaList(userId, page)

    suspend fun getUserFollowList(userId: String, page: Int) =
        userApi.getUserFollowList(userId, page)

    suspend fun getUserFansList(userId: String, page: Int) = userApi.getUserFansList(userId, page)
}