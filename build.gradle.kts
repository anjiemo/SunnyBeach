// SunnyBeach-Kotlin 版本
// Gradle 配置已重构为 build-logic 约定插件模式

import java.util.Properties

// 导入配置文件
apply(from = "configs.gradle.kts")

// 读取版本配置
val versionPropsFile = file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) {
        versionPropsFile.inputStream().use { load(it) }
    }
}

// 优先使用环境变量（非空时），否则使用 version.properties 中的值
val appVersionCode: Int = (System.getenv("APP_VERSION_CODE")?.takeIf { it.isNotBlank() }
    ?: versionProps.getProperty("versionCode", "22")).toInt()
val appVersionName: String = System.getenv("APP_VERSION_NAME")?.takeIf { it.isNotBlank() }
    ?: versionProps.getProperty("versionName", "5.4.1")

// 将版本信息存储到 extra 供子模块使用
extra["appVersionCode"] = appVersionCode
extra["appVersionName"] = appVersionName

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // AndroidAOP 插件（不在 Gradle Plugin Portal 中，必须使用 buildscript 方式）
        classpath(libs.androidaop.plugin)
    }
}

plugins {
    // Gradle 插件版本说明：https://developer.android.google.cn/studio/releases/gradle-plugin.html#updating-plugin
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    // Kotlin 插件：https://plugins.jetbrains.com/plugin/6954-kotlin
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // KSP插件：https://developer.android.com/build/migrate-to-ksp?hl=zh-cn#add-ksp
    // https://kotlinlang.org/docs/ksp-quickstart.html#add-a-processor
    // https://github.com/google/ksp/releases
    alias(libs.plugins.ksp) apply false
    // Hilt 插件：https://dagger.dev/hilt/gradle-setup
    alias(libs.plugins.hilt) apply false
}
