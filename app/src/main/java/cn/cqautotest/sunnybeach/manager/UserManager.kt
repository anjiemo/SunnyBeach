package cn.cqautotest.sunnybeach.manager

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.event.LiveBusKeyConfig
import cn.cqautotest.sunnybeach.event.LiveBusUtils
import cn.cqautotest.sunnybeach.ktx.fromJson
import cn.cqautotest.sunnybeach.ktx.toJson
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.util.AUTO_LOGIN
import cn.cqautotest.sunnybeach.util.SOB_ACCOUNT
import cn.cqautotest.sunnybeach.util.SOB_PASSWORD
import cn.cqautotest.sunnybeach.util.SOB_REMEMBER_PWD
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_USER_BASIC_INFO
import com.tencent.mmkv.MMKV

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/01
 * desc   : 用户管理者
 */
object UserManager {

    private val appContext = AppApplication.getInstance()
    private val mmkv by lazy { MMKV.defaultMMKV() }

    /**
     * 退出账户登录
     */
    fun exitUserAccount() {
        mmkv.removeValueForKey(SUNNY_BEACH_USER_BASIC_INFO)
        LiveBusUtils.busSend(LiveBusKeyConfig.BUS_LOGIN_INFO_UPDATE)
    }

    /**
     * 用户是否已经成功登录过一次了
     */
    fun isLogin() = loadUserBasicInfo() != null

    /**
     * 获取是否自动登录
     */
    fun isAutoLogin(): Boolean {
        return mmkv.getBoolean(AUTO_LOGIN, false)
    }

    /**
     * 设置用户是否自动登录
     */
    fun setupAutoLogin(autoLogin: Boolean) {
        mmkv.putBoolean(AUTO_LOGIN, autoLogin)
    }

    /**
     * 保存用户基本信息
     */
    fun saveUserBasicInfo(userBasicInfo: UserBasicInfo?) {
        mmkv.putString(SUNNY_BEACH_USER_BASIC_INFO, userBasicInfo?.toJson())
        LiveBusUtils.busSend(LiveBusKeyConfig.BUS_LOGIN_INFO_UPDATE)
    }

    /**
     * 获取用户基本信息
     */
    fun loadUserBasicInfo(): UserBasicInfo? {
        return try {
            val jsonByUserBasicInfo = mmkv.getString(SUNNY_BEACH_USER_BASIC_INFO, null)
            fromJson(jsonByUserBasicInfo)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    /**
     * 获取当前用户的 id
     */
    fun loadCurrUserId() = loadUserBasicInfo()?.id.orEmpty()

    /**
     * 根据 userId 判断是否为当前用户
     * false：不是当前登录的用户
     * true：是当前登录的用户
     */
    fun isCurrUser(userId: String) = userId == loadCurrUserId()

    /**
     * 不是当前用户：
     * true：不是当前登录的用户
     * false：是当前登录的用户
     */
    fun isNotCurrUser(userId: String) = !isCurrUser(userId)

    /**
     * 当前用户是否为 vip
     */
    fun currUserIsVip() = loadUserBasicInfo()?.isVip.equals("1")

    /**
     * 获取用户头像挂件
     * vip：特殊头像挂件
     * guest：普通头像挂件（或者没有头像挂件）
     */
    fun getAvatarPendant(vip: Boolean): Drawable? {
        return if (vip) ContextCompat.getDrawable(appContext, R.drawable.avatar_circle_vip_ic)
        else null
    }

    /**
     * 获取用户昵称颜色
     * vip：粉色
     * guest：黑色
     */
    fun getNickNameColor(vip: Boolean): Int {
        return if (vip) ContextCompat.getColor(appContext, R.color.pink)
        else Color.BLACK
    }

    /**
     * 保存当前登录过的账号
     */
    fun saveCurrLoginAccount(account: String) {
        mmkv.putString(SOB_ACCOUNT, account)
    }

    /**
     * 获取当前登录过的账号
     */
    fun getCurrLoginAccount(): String {
        return mmkv.getString(SOB_ACCOUNT, "").orEmpty()
    }

    /**
     * 保存当前登录过的账号密码
     */
    fun saveCurrLoginAccountPassword(account: String) {
        mmkv.putString(SOB_PASSWORD, account)
    }

    /**
     * 获取当前登录过的账号密码
     */
    fun getCurrLoginAccountPassword(): String {
        return mmkv.getString(SOB_PASSWORD, "").orEmpty()
    }

    /**
     * 保存是否记住密码的状态
     */
    fun isRememberPwd(remember: Boolean) {
        mmkv.putBoolean(SOB_REMEMBER_PWD, remember)
    }

    /**
     * 是否记住密码
     */
    fun isRememberPwd(): Boolean {
        return mmkv.getBoolean(SOB_REMEMBER_PWD, true)
    }
}