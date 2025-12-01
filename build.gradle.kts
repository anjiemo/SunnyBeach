// SunnyBeach-Kotlin 版本

import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// 导入配置文件
apply(from = "configs.gradle.kts")

buildscript {
    repositories {
        google()
        mavenCentral()
        // 华为仓库
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
    dependencies {
        // 华为 AGConnect 插件
        classpath("com.huawei.agconnect:agcp:1.9.4.300")
        // AndroidAOP 插件
        classpath("io.github.flyjingfish:androidaop-plugin:2.7.3")
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

allprojects {
    repositories {
        // 友盟远程仓库：https://info.umeng.com/detail?id=443&cateId=1
        maven { url = uri("https://repo1.maven.org/maven2") }
        // 添加通用的 Maven 仓库配置
        addCommonMaven()
    }

    tasks.withType<JavaCompile>().configureEach {
        // 设置全局编码
        options.encoding = "UTF-8"
    }
    tasks.withType<Javadoc>().configureEach {
        // 设置文档编码
        options {
            encoding = "UTF-8"
            (this as StandardJavadocDocletOptions).apply {
                charSet = "UTF-8"
                links("http://docs.oracle.com/javase/7/docs/api")
            }
        }
    }

    // 将构建文件统一输出到项目根目录下的 build 文件夹
    layout.buildDirectory.set(file("$rootDir/build/${path.replace(":", "/")}"))
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

/**
 * 添加通用的 Maven 仓库配置
 */
fun RepositoryHandler.addCommonMaven() {
    // MavenCentral 远程仓库：https://mvnrepository.com
    mavenCentral()
    // 阿里云云效仓库：https://maven.aliyun.com/mvn/guide
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    // 华为开源镜像：https://mirrors.huaweicloud.com
    maven { url = uri("https://repo.huaweicloud.com/repository/maven") }
    // JitPack 远程仓库：https://jitpack.io
    maven { url = uri("https://jitpack.io") }
    google()
    // 配置HMS Core SDK的Maven仓地址
    maven { url = uri("https://developer.huawei.com/repo/") }
    // 阿里云视频SDK所在的仓库
    maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/releases") }
}

subprojects {
    // 统一配置 Android 模块
    // 只要应用了 com.android.application 或 com.android.library 插件，就会应用此配置
    pluginManager.withPlugin("com.android.base") {
        // 为所有 Android 模块应用 Kotlin 插件
        apply(plugin = "org.jetbrains.kotlin.android")

        // 配置 Android 扩展
        extensions.configure<BaseExtension> {
            compileSdkVersion(36)

            defaultConfig {
                minSdkVersion(26)
                targetSdkVersion(33)
                versionName = "5.4.1"
                versionCode = 22
            }

            // 支持 Java JDK 21
            compileOptions {
                targetCompatibility = JavaVersion.VERSION_21
                sourceCompatibility = JavaVersion.VERSION_21
            }

            buildFeatures.viewBinding = true
            // 只有 app 模块才生成 BuildConfig
            buildFeatures.buildConfig = (project.name == "app")

            // 设置存放 so 文件的目录
            sourceSets.getByName("main") {
                jniLibs.srcDirs("libs")
            }

            // 可在 Studio 最左侧中的 Build Variants 选项中切换默认的构建类型
            buildTypes {
                getByName("debug") {}
                create("preview") {}
                getByName("release") {}
            }

            // 代码警告配置
            lintOptions {
                // 禁用文本硬编码警告
                // 禁用图片描述警告
                disable("HardcodedText", "ContentDescription")
            }
        }

        // 配置 Kotlin 编译选项
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_21)
                freeCompilerArgs.add("-Xcontext-parameters")
            }
        }

        // 通用依赖配置（排除 library:base，因为它使用 api 依赖）
        if (project.name != "base") {
            dependencies {
                // 依赖 libs 目录下所有的 jar 和 aar 包
                add("implementation", fileTree(mapOf("include" to listOf("*.jar", "*.aar"), "dir" to "libs")))

                // AndroidX 库
                add("implementation", "androidx.appcompat:appcompat:1.7.1")
                // Material 库
                add("implementation", "com.google.android.material:material:1.13.0")

                // Kotlin 协程
                add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
                // AndroidX 生命周期库
                add("implementation", "androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")
                add("implementation", "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
                add("implementation", "androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
            }
        }
    }

    // 针对 Library 模块的特殊配置
    pluginManager.withPlugin("com.android.library") {
        afterEvaluate {
            // 排除名为 umeng 的 Module 工程
            if (name != "umeng") {
                // 排除 BuildConfig.class
                try {
                    tasks.named("generateReleaseBuildConfig").configure { enabled = false }
                    tasks.named("generatePreviewBuildConfig").configure { enabled = false }
                    tasks.named("generateDebugBuildConfig").configure { enabled = false }
                } catch (_: Exception) {
                    // 忽略任务不存在的异常
                }
            }
        }
    }
}
