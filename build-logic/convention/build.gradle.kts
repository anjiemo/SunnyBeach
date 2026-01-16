// SunnyBeach 约定插件模块
// 提供统一的 Android 项目配置

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "cn.cqautotest.sunnybeach.buildlogic"

// 配置 build-logic 插件使用 JDK 21
// 与主项目保持一致
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        // Android Application 约定插件
        register("androidApplication") {
            id = "sunnybeach.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        // Android Library 约定插件
        register("androidLibrary") {
            id = "sunnybeach.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        // Android Compose 约定插件
        register("androidCompose") {
            id = "sunnybeach.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        // Hilt 依赖注入约定插件
        register("hilt") {
            id = "sunnybeach.hilt"
            implementationClass = "HiltConventionPlugin"
        }
    }
}
