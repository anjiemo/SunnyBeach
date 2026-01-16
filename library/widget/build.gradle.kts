plugins {
    // 使用 SunnyBeach Android Library 约定插件
    alias(libs.plugins.sunnybeach.android.library)
}

android {
    namespace = "com.hjq.widget"
}

dependencies {
    // 基础库（不包任何第三方框架）
    implementation(project(":library:base"))
}
