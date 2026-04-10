/**
 * Android Library 约定插件
 * 为 library 模块提供统一的 Library 配置
 */

import cn.cqautotest.sunnybeach.configureKotlinAndroid
import cn.cqautotest.sunnybeach.libs
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用 Android Library 插件
            apply(plugin = "com.android.library")

            // 配置 Android Library 扩展
            extensions.configure<LibraryExtension> {
                // 应用通用 Kotlin Android 配置
                configureKotlinAndroid(this)
                // Library 模块默认不生成 BuildConfig
                buildFeatures {
                    buildConfig = false
                }
            }

            // 通用依赖配置（排除 library:base，因为它使用 api 依赖）
            if (project.name != "base") {
                dependencies {
                    // 依赖 libs 目录下所有的 jar 和 aar 包
                    add("implementation", fileTree(mapOf("include" to listOf("*.jar", "*.aar"), "dir" to "libs")))

                    // AndroidX 库
                    add("implementation", libs.findLibrary("androidx-appcompat").get())
                    // Material 库
                    add("implementation", libs.findLibrary("material").get())

                    // Kotlin 协程
                    add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())
                    add("implementation", libs.findLibrary("kotlinx-coroutines-android").get())
                    // AndroidX 生命周期库
                    add("implementation", libs.findLibrary("androidx-lifecycle-livedata-ktx").get())
                    add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                    add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-ktx").get())
                }
            }
        }
    }
}
