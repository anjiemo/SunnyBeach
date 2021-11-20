package cn.cqautotest.sunnybeach.manager

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.core.content.ContextCompat
import cn.cqautotest.sunnybeach.R
import cn.cqautotest.sunnybeach.app.AppApplication
import cn.cqautotest.sunnybeach.model.UserBasicInfo
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_USER_BASIC_INFO
import cn.cqautotest.sunnybeach.util.fromJson
import com.tencent.mmkv.MMKV

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/11/01
 * desc   : 用户管理者
 */
object UserManager {

    private val appContext = AppApplication.getInstance()

    /**
     * 退出账户登录
     */
    fun exitUserAccount() {
        val mmkv = MMKV.defaultMMKV() ?: return
        mmkv.removeValueForKey(SUNNY_BEACH_USER_BASIC_INFO)
    }

    /**
     * 获取用户基本信息
     */
    fun loadUserBasicInfo(): UserBasicInfo? {
        val mmkv = MMKV.defaultMMKV() ?: return null
        return runCatching {
            val jsonByUserBasicInfo = mmkv.getString(SUNNY_BEACH_USER_BASIC_INFO, null)
            fromJson<UserBasicInfo>(jsonByUserBasicInfo)
        }.getOrNull()
    }

    /**
     * 获取当前用户的 id
     */
    fun loadCurrUserId() = loadUserBasicInfo()?.id ?: ""

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
    fun currUserIsVip() = if (TextUtils.isEmpty(loadCurrUserId())) false
    else loadUserBasicInfo()?.isVip == "1"

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
}