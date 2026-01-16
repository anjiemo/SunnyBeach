/**
 * Kotlin Android 通用配置
 * 提供 Android 模块的基础配置函数
 */

package cn.cqautotest.sunnybeach

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * 配置 Kotlin Android 基础选项
 * 包括 compileSdk、minSdk、compileOptions、Kotlin 编译选项等
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig {
            minSdk = 26
        }

        // 支持 Java JDK 21
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        // 启用 ViewBinding
        buildFeatures.viewBinding = true

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
        lint {
            // 禁用文本硬编码警告
            // 禁用图片描述警告
            disable += setOf("HardcodedText", "ContentDescription")
        }
    }

    configureKotlin()
}

/**
 * 配置 Kotlin 编译选项
 */
internal fun Project.configureKotlin() {
    // 配置 Kotlin 编译选项
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}

/**
 * 配置 Java 工具链
 */
internal fun Project.configureJavaToolchain() {
    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(21))
        }
    }
}
