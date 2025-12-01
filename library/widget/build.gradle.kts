plugins {
    id("com.android.library")
}

android {
    namespace = "com.hjq.widget"
}

dependencies {
    // 基础库（不包任何第三方框架）
    implementation(project(":library:base"))
}
