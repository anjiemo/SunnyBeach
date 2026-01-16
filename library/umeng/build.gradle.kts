plugins {
    // 使用 SunnyBeach Android Library 约定插件
    alias(libs.plugins.sunnybeach.android.library)
}

android {
    namespace = "com.hjq.umeng"

    defaultConfig {
        // 模块混淆配置
        consumerProguardFiles("proguard-umeng.pro")

        // 构建配置字段
        resValue("string", "um_key", rootProject.extra["UMENG_APP_KEY"] as String)
        resValue("string", "wx_id", rootProject.extra["WX_APP_ID"] as String)
        resValue("string", "wx_secret", rootProject.extra["WX_APP_SECRET"] as String)

        // 清单占位符
        manifestPlaceholders["UM_KEY"] = rootProject.extra["UMENG_APP_KEY"] as String
        manifestPlaceholders["WX_ID"] = rootProject.extra["WX_APP_ID"] as String
        manifestPlaceholders["WX_SECRET"] = rootProject.extra["WX_APP_SECRET"] as String
    }

    // umeng 模块需要生成 BuildConfig
    buildFeatures.buildConfig = true
}

// 友盟统计集成文档：https://developer.umeng.com/docs/119267/detail/118584
// 友盟社会化集成文档：https://developer.umeng.com/docs/128606/detail/193879
dependencies {
    // 友盟公共库
    api(libs.umeng.common)
    api(libs.umeng.asms)
    // 友盟分享库
    api(libs.umeng.share.core)
    // 友盟微信分享
    api(libs.umeng.share.wx)

    // 微信组件：https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html
    api(libs.wechat.sdk)
}
