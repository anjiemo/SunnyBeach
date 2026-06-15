@file:Suppress("UnstableApiUsage")

pluginManagement {
    // 包含 build-logic 约定插件模块
    includeBuild("build-logic")
    repositories {
        val isCiBuild = System.getenv("CI_BUILD").orEmpty().toBoolean()
        fun ArtifactRepository.excludeUnmirrored() {
            content { excludeGroup("com.android.tools.studio.leakcanary") }
        }
        if (isCiBuild) {
            // Nothing to do.
        } else {
            // 国内镜像优先
            // 阿里云云效仓库（Gradle 插件）：https://maven.aliyun.com/mvn/guide
            maven {
                url = uri("https://maven.aliyun.com/repository/gradle-plugin")
                excludeUnmirrored()
            }
            maven {
                url = uri("https://maven.aliyun.com/repository/public")
                excludeUnmirrored()
            }
            maven {
                url = uri("https://repo.huaweicloud.com/repository/maven")
                excludeUnmirrored()
            }
            maven {
                url = uri("https://maven.aliyun.com/repository/google")
                excludeUnmirrored()
            }
        }
        gradlePluginPortal()
        // MavenCentral 远程仓库：https://mvnrepository.com
        mavenCentral()
        // 官方镜像备用
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        val isCiBuild = System.getenv("CI_BUILD").orEmpty().toBoolean()
        fun ArtifactRepository.excludeUnmirrored() {
            content { excludeGroup("com.android.tools.studio.leakcanary") }
        }
        if (isCiBuild) {
            // Nothing to do.
        } else {
            // 国内镜像
            // 阿里云云效仓库：https://maven.aliyun.com/mvn/guide
            maven {
                url = uri("https://maven.aliyun.com/repository/public")
                excludeUnmirrored()
            }
            maven {
                url = uri("https://maven.aliyun.com/repository/google")
                excludeUnmirrored()
            }
            // 华为开源镜像：https://mirrors.huaweicloud.com
            maven {
                url = uri("https://repo.huaweicloud.com/repository/maven")
                excludeUnmirrored()
            }
        }
        // JitPack 远程仓库：https://jitpack.io
        maven { url = uri("https://jitpack.io") }
        // MavenCentral 远程仓库：https://mvnrepository.com
        mavenCentral()
        // 官方镜像
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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