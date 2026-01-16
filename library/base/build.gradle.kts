plugins {
    // 使用 SunnyBeach Android Library 约定插件
    alias(libs.plugins.sunnybeach.android.library)
}

android {
    namespace = "com.hjq.base"

    defaultConfig {
        // 模块混淆配置
        consumerProguardFiles("proguard-base.pro")
    }

    sourceSets {
        getByName("main") {
            // res 资源目录配置
            res.srcDirs(
                "src/main/res",
                "src/main/res-sw"
            )
        }
    }
}
dependencies {
    // 暴露 AndroidX 和 Material 库给上层模块，解决 Hilt/KSP 无法识别基类的问题
    api(libs.androidx.appcompat)
    api(libs.material)
}
