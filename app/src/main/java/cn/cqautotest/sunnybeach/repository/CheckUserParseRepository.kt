package cn.cqautotest.sunnybeach.repository

import cn.cqautotest.sunnybeach.util.I_LOVE_ANDROID_SITE_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_SITE_BASE_URL
import cn.cqautotest.sunnybeach.util.StringUtil
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2025/10/16
 * desc   : 检查用户信息解析 Repository
 */
class CheckUserParseRepository {

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
}