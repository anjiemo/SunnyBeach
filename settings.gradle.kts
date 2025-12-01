pluginManagement {
    repositories {
        // 优先使用国内镜像，可以提升下载速度
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        gradlePluginPortal()
        google()
        mavenCentral()
        // 华为插件仓库，解决 'com.huawei.agconnect' 插件找不到的问题
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        // 优先使用国内镜像
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        google()
        mavenCentral()
        // 华为相关仓库
        maven { url = uri("https://developer.huawei.com/repo/") }
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") }
        // 其他仓库
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/releases") }
    }
}

rootProject.name = "SunnyBeach"
include(":app")
include(":library:base")
include(":library:widget")
include(":library:umeng")
include(":library:network")