plugins {
    // 使用 SunnyBeach Android Library 约定插件
    alias(libs.plugins.sunnybeach.android.library)
}

android {
    namespace = "cn.funkt.network"
}

dependencies {
    // 单元测试
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // OkHttp 框架：https://github.com/square/okhttp
    compileOnly(libs.okhttp)
    // Android网络请求库：https://github.com/square/retrofit
    compileOnly(libs.retrofit)
}
