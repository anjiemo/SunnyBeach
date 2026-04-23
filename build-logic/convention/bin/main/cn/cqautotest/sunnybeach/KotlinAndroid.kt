/**
 * Kotlin Android 通用配置
 * 提供 Android 模块的基础配置函数
 */

package cn.cqautotest.sunnybeach

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * 配置 Kotlin Android 基础选项
 * 适配现代 AGP：通过公共 DSL 接口配置通用选项
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig.minSdk = 26

        compileOptions.sourceCompatibility = JavaVersion.VERSION_21
        compileOptions.targetCompatibility = JavaVersion.VERSION_21

        // 处理通用 buildFeatures 配置
        buildFeatures.apply {
            viewBinding = true
            resValues = true
        }

        // 设置存放 so 文件的目录
        sourceSets.getByName("main") {
            jniLibs {
                directories += "libs"
            }
        }

        buildTypes {
            getByName("debug") {}
            create("preview") {}
            getByName("release") {}
        }

        // 代码警告配置
        lint.disable.addAll(setOf("HardcodedText", "ContentDescription"))
    }

    configureKotlin()
    configureJavaEncoding()
}

/**
 * 配置 Kotlin 编译选项
 */
internal fun Project.configureKotlin() {
    // 配置 Kotlin 编译选项
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            // 启用现代 Kotlin 特性
            freeCompilerArgs.add("-Xcontext-parameters")
        }
    }
}

/**
 * 配置 Java 编码为 UTF-8
 */
private fun Project.configureJavaEncoding() {
    tasks.withType<JavaCompile>().configureEach {
        // 设置全局编码
        options.encoding = "UTF-8"
    }
}

/**
 * 配置 Android 模块的基础依赖
 */
internal fun DependencyHandlerScope.configureCommonDependencies(libs: VersionCatalog) {
    add("implementation", libs.findLibrary("androidx-core-ktx").get())
    add("implementation", libs.findLibrary("androidx-activity-ktx").get())
    add("implementation", libs.findLibrary("androidx-fragment-ktx").get())
    add("implementation", libs.findLibrary("androidx-appcompat").get())
    add("implementation", libs.findLibrary("material").get())

    // Kotlin 协程
    add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
    add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())

    // AndroidX 生命周期库
    add("implementation", libs.findLibrary("androidx-lifecycle-livedata-ktx").get())
    add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
    add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-ktx").get())
}
