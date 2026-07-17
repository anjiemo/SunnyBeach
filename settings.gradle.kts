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
        // 官方仓库优先，避免 androidx 等依赖误走 JitPack 导致 CI 超时
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // MavenCentral 远程仓库：https://mvnrepository.com
        mavenCentral()
        // JitPack 远程仓库：https://jitpack.io
        // JitPack 仅解析 GitHub 托管的第三方库（getActivity、androidaop 等）
        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroupByRegex("com\\.github\\..*")
                includeGroup("io.github.flyjingfish")
                includeGroup("com.guolindev.glance")
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

// 自动启用版本化 git hooks（.githooks/ 下的提交署名校验）。
// core.hooksPath 是本地 config，无法随仓库提交；放在配置期执行 → 每次 sync / build 自动校正，
// 开发者无需记忆 `git config core.hooksPath .githooks`。
// 幂等：已设置则不重复写入。整体 runCatching 包住——没装 git、源码 zip 等非 git 环境不能让 sync 失败。
// 跳过 CI：CI 不需要本地 hook。
if (System.getenv("CI").isNullOrBlank() && rootDir.resolve(".git").exists()) {
    val hooksDir = rootDir.resolve(".githooks")
    if (hooksDir.isDirectory) {
        runCatching {
            val current = providers.exec {
                commandLine("git", "config", "--get", "core.hooksPath")
                isIgnoreExitValue = true
            }.standardOutput.asText.get().trim()
            if (current != ".githooks") {
                providers.exec {
                    commandLine("git", "config", "core.hooksPath", ".githooks")
                }.result.get()
            }
        }
    }
}

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_21)) {
    """
    SunnyBeach requires JDK 21+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}