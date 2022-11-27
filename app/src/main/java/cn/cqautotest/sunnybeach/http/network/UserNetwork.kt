package cn.cqautotest.sunnybeach.http.network

import cn.cqautotest.sunnybeach.http.api.sob.UserApi
import cn.cqautotest.sunnybeach.model.*
import okhttp3.MultipartBody

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 用户信息获取
 */
object UserNetwork {

    suspend fun searchQuestionList(page: Int, size: Int, questionSearchFilter: QuestionSearchFilter) =
        UserApi.searchQuestionList(page, size, questionSearchFilter)

    suspend fun searchArticleList(page: Int, articleSearchFilter: ArticleSearchFilter) =
        UserApi.searchArticleList(page, articleSearchFilter)

    suspend fun queryIntegralRule(item: String) = UserApi.queryIntegralRule(item)

    suspend fun report(report: Report) = UserApi.report(report)

    suspend fun modifyAvatar(avatarUrl: String) = UserApi.modifyAvatar(avatarUrl)

    suspend fun uploadUserCenterImageByCategoryId(part: MultipartBody.Part, categoryId: String) =
        UserApi.uploadUserCenterImageByCategoryId(part, categoryId)

    suspend fun sendEmail(email: String) = UserApi.sendEmail(email)

    suspend fun modifyPasswordBySms(smsCode: String, user: User) = UserApi.modifyPasswordBySms(smsCode, user)

    suspend fun modifyPasswordByOldPwd(modifyPwd: ModifyPwd) = UserApi.modifyPasswordByOldPwd(modifyPwd)

    suspend fun checkSmsCode(phoneNumber: String, smsCode: String) = UserApi.checkSmsCode(phoneNumber, smsCode)

    suspend fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = UserApi.sendForgetSmsVerifyCode(smsInfo)

    suspend fun registerAccount(smsCode: String, user: User) = UserApi.registerAccount(smsCode, user)

    suspend fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = UserApi.sendRegisterSmsVerifyCode(smsInfo)

    suspend fun getSobIEDetailList(userId: String, page: Int) = UserApi.getSobIEDetailList(userId, page)

    suspend fun getVipUserList() = UserApi.getVipUserList()

    suspend fun getAchievement() = UserApi.getAchievement()

    suspend fun queryTotalSobCount() = UserApi.queryTotalSobCount()

    suspend fun modifyUserInfo(personCenterInfo: PersonCenterInfo) = UserApi.modifyUserInfo(personCenterInfo)

    suspend fun queryUserInfo() = UserApi.queryUserInfo()

    suspend fun getRichList(count: Int = 100) = UserApi.getRichList(count)

    suspend fun queryUserAvatar(account: String) = UserApi.queryUserAvatar(account)

    suspend fun logout() = UserApi.logout()

    suspend fun checkToken() = UserApi.checkToken()

    suspend fun login(captcha: String, user: User) = UserApi.login(captcha, user)

    suspend fun getAchievement(userId: String) = UserApi.getAchievement(userId)

    suspend fun getUserInfo(userId: String) = UserApi.getUserInfo(userId)

    suspend fun followState(userId: String) = UserApi.followState(userId)

    suspend fun followUser(userId: String) = UserApi.followUser(userId)

    suspend fun unfollowUser(userId: String) = UserApi.unfollowUser(userId)

    suspend fun checkAllowance() = UserApi.checkAllowance()

    suspend fun getAllowance() = UserApi.getAllowance()
}