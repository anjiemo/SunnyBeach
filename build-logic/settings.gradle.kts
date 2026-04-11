@file:Suppress("UnstableApiUsage")
// build-logic 模块设置
// 此模块为 SunnyBeach 项目提供约定插件

pluginManagement {
    repositories {
        // Gradle 插件门户：https://plugins.gradle.org
        gradlePluginPortal()
        google()
        // MavenCentral 远程仓库：https://mvnrepository.com
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        // MavenCentral 远程仓库：https://mvnrepository.com
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
