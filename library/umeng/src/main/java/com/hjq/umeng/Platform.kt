package com.hjq.umeng

import com.umeng.socialize.bean.SHARE_MEDIA

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/04/03
 *    desc   : 友盟平台
 */
enum class Platform(
    /** 第三方平台 */
    private val thirdParty: SHARE_MEDIA?,
    /** 第三方包名 */
    private val packageName: String?
) {
    /** 微信 */
    WECHAT(SHARE_MEDIA.WEIXIN, "com.tencent.mm"),

    /** 微信朋友圈 */
    CIRCLE(SHARE_MEDIA.WEIXIN_CIRCLE, "com.tencent.mm");

    fun getThirdParty(): SHARE_MEDIA? {
        return thirdParty
    }

    fun getPackageName(): String? {
        return packageName
    }
}