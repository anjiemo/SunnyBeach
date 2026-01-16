plugins {
    // 使用 SunnyBeach Android Library 约定插件
    alias(libs.plugins.sunnybeach.android.library)
}

android {
    namespace = "cn.funkt.network"
}

dependencies {
    // 单元测试
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // OkHttp 框架：https://github.com/square/okhttp
    compileOnly("com.squareup.okhttp3:okhttp")
    // Android网络请求库：https://github.com/square/retrofit
    compileOnly("com.squareup.retrofit2:retrofit:3.0.0")
}
