@file:Suppress("UnstableApiUsage")
pluginManagement {
    // 包含 build-logic 约定插件模块
    includeBuild("build-logic")
    repositories {
        // 优先使用官方仓库，确保能获取最新插件
        gradlePluginPortal()
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        // 国内镜像作为备用，可以提升下载速度
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 优先使用核心仓库
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // MavenCentral 远程仓库：https://mvnrepository.com
        mavenCentral()
        // 阿里云云效仓库：https://maven.aliyun.com/mvn/guide
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // 华为开源镜像：https://mirrors.huaweicloud.com
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") }
        // JitPack 远程仓库：https://jitpack.io
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "SunnyBeach"
include(":app")
include(":library:base")
include(":library:widget")
include(":library:umeng")
include(":library:network")

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_21)) {
    """
    SunnyBeach requires JDK 21+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}