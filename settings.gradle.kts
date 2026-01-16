pluginManagement {
    // 包含 build-logic 约定插件模块
    includeBuild("build-logic")
    repositories {
        // 优先使用官方仓库，确保能获取最新插件
        gradlePluginPortal()
        google()
        mavenCentral()
        // 国内镜像作为备用，可以提升下载速度
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        // 华为插件仓库，解决 'com.huawei.agconnect' 插件找不到的问题
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        // 阿里云云效仓库：https://maven.aliyun.com/mvn/guide
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        // 华为开源镜像：https://mirrors.huaweicloud.com
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") }
        // JitPack 远程仓库：https://jitpack.io
        maven { url = uri("https://jitpack.io") }
        // MavenCentral 远程仓库：https://mvnrepository.com
        mavenCentral()
        google()
        // 配置HMS Core SDK的Maven仓地址
        maven { url = uri("https://developer.huawei.com/repo/") }
        // 阿里云视频SDK所在的仓库
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/releases") }
    }
}

rootProject.name = "SunnyBeach"
include(":app")
include(":library:base")
include(":library:widget")
include(":library:umeng")
include(":library:network")